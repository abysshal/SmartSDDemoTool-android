package SmartSD.DemoTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import Incomm.Library.SmartSDLib;
import SmartSD.DemoTool.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SmartSD extends Activity {

	public static final int CASE4_FIX_DATA_LEN = 500;
	public static final int CASE4_TEST_BLOCK_NUM = 128;// 128;
	public static final int SMARTSD_MAX_RETRY_NUM = 10;
	public static final char FixedDataLength = 1;

	public static SmartSD instance = null;

	SmartSDLib SDLib = new SmartSDLib();

	char Cmd[] = { 0x00, 0x20, 0x00, 0x00, 0x00 };
	char CmdPPS[] = { 0xFF, 0x10, 0x12, 0xFD };
	char cData[] = new char[512];
	char cRet;
	char cIndex;

	int intAPDU_len[] = new int[1];
	int intRetLen = 2;
	int iUpdateStatusFlag = 0;
	int intOnOff = 1;
	int intPPSIdx = 2;
	int giReturnValue;

	char SPI_CMD_500[] = { 0x00, 0x80, 0x01, FixedDataLength, 0x00 };
	char SPI_END_500[] = { 0x00, 0x8F, 0x01, FixedDataLength, 0x00 };
	char SPI_CMD_500_E[] = { 0x00, 0x80, 0x00, FixedDataLength, 0x00 };
	char SPI_END_500_E[] = { 0x00, 0x8F, 0x00, FixedDataLength, 0x00 };
	char inbuf[] = new char[(CASE4_FIX_DATA_LEN + 12) * 8];
	char outbuf[] = new char[(CASE4_FIX_DATA_LEN + 12) * 8];
	char ITRUST[] = { 'I', 'T', 'R', 'U', 'S', 'T' };

	int iPPSIdx;
	String sTemp = "";
	String strCMD = "";
	char cCmd[];

	Button send, get, reset, initial, info, format, endTM, getBlock, access,
			setpps, clear, handle61, speed, enFixLen, sendData, getData,
			runapp, btnSend, smsSend;

	EditText EditTextCMD, EditTextLoop, EditTextIndex, smsToEditText,
			smsBodyEditText;

	TextView showInfo;

	Handler showInfoHandler;

	StringBuffer strRet = new StringBuffer();
	String str9000 = "9000";
	String strTemp;
	String strErr = "Err";
	String showStatus;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		instance = this;

		send = (Button) findViewById(R.id.send);
		get = (Button) findViewById(R.id.get);
		reset = (Button) findViewById(R.id.reset);
		initial = (Button) findViewById(R.id.initial);
		info = (Button) findViewById(R.id.Info);
		format = (Button) findViewById(R.id.format);
		endTM = (Button) findViewById(R.id.end);
		getBlock = (Button) findViewById(R.id.getblock);
		access = (Button) findViewById(R.id.access);
		setpps = (Button) findViewById(R.id.setpps);
		speed = (Button) findViewById(R.id.speed);
		clear = (Button) findViewById(R.id.clear);
		handle61 = (Button) findViewById(R.id.handle61);
		enFixLen = (Button) findViewById(R.id.enFixDataLen);
		sendData = (Button) findViewById(R.id.sendData);
		getData = (Button) findViewById(R.id.getData);
		runapp = (Button) findViewById(R.id.runapp);
		btnSend = (Button) findViewById(R.id.btnSend);
		smsSend = (Button) findViewById(R.id.smsBtnSend1);

		showInfo = (TextView) findViewById(R.id.showInfo);

		EditTextCMD = (EditText) findViewById(R.id.CMDEditText);
		EditTextIndex = (EditText) findViewById(R.id.index);
		EditTextLoop = (EditText) findViewById(R.id.EditTextLoopTimes);
		smsToEditText = (EditText) findViewById(R.id.smsToEditText);
		smsBodyEditText = (EditText) findViewById(R.id.smsBodyEditText);

		showInfoHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0)
					showInfo.setText(showStatus);
			}
		};
		// Do Initialization
		initial.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_Initialization(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						.toString());
				if (cRet == 0x00) {
					strRet.append("Initial OK!!\n");
				} else {
					strRet.append("Initial FAIL!!\n" + strErr
							+ Integer.toHexString(cRet) + "\n");
				}

				updateStatus(strRet);
			}
		});
		// Do Reset
		reset.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_Reset(intAPDU_len, cData);
				if (cRet == 0x00) {
					strRet.append("Reset OK!!\n"
							+ To_Hex(cData, intAPDU_len[0]));
				} else {
					strRet.append("Reset FAIL!!\n" + "Err "
							+ Integer.toHexString(cRet) + "\n");
				}

				updateStatus(strRet);
			}
		});
		// Get Information
		info.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_GetCID(cData);
				if (cRet == 0x00)
					strRet.append("CID: " + To_Hex(cData, 16) + "\n");
				else {
					strRet.append("CID: FAIL\n" + strErr
							+ Integer.toHexString(cRet) + "\n");
				}

				SDLib.InCOMM_SmartSDCard_GetDLLVersion(cData);
				strRet.append("DLL Ver.:" + String.valueOf(cData, 0, 16) + "\n");

				cRet = SDLib.InCOMM_SmartSDCard_GetCardVersion(cData);
				if (cRet == 0x00)
					strRet.append("Card Ver.: " + String.valueOf(cData, 0, 17)
							+ "\n");
				else
					strRet.append("Card Ver.: FAIL\n" + strErr
							+ Integer.toHexString(cRet) + "\n");

				updateStatus(strRet);
			}
		});
		// Do Format
		format.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_Format();
				if (cRet == 0x00)
					strRet.append("Format Done!!\n");
				else
					strRet.append("Format FAIL!!\n" + strErr
							+ Integer.toHexString(cRet) + "\n");

				updateStatus(strRet);
			}
		});
		// Do end Transmission
		endTM.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_Endtransmission();
				if (cRet == 0x00)
					strRet.append("EndTM Done!!\n");
				else
					strRet.append("EndTM FAIL!!\n" + strErr
							+ Integer.toHexString(cRet) + "\n");

				updateStatus(strRet);
			}
		});
		// Send Command
		send.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				sTemp = String.valueOf(EditTextCMD.getText());
				for (int i = 0; i < sTemp.length(); i = i + 2) {
					cCmd = sTemp.substring(i, i + 2).toCharArray();
					if (StrToHex(cCmd) == true) {
						strCMD += new Character((char) giReturnValue)
								.toString();
					}
				}

				cRet = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(
						strCMD.toCharArray().length, strCMD.toCharArray());

				strRet.append("\nSend CMD: ");
				if (cRet == 0x00)
					strRet.append("Send Cmd " + sTemp + " OK!!\n");
				else
					strRet.append("Send Cmd " + sTemp + " Faill!! Err "
							+ Integer.toHexString(cRet) + "\n");

				updateStatus(strRet);
			}
		});
		// Get Response(Normal Mode)
		get.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
						cData);
				if (cRet == 0x00) {
					strRet.append("Ret: " + To_Hex(cData, intAPDU_len[0]));

				} else {
					strRet.append(strErr + Integer.toHexString(cRet) + "\n");
				}

				updateStatus(strRet);
			}
		});
		// Get Response(Block Mode)
		getBlock.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand_Block(
						intAPDU_len, cData, 0);
				if (cRet == 0x00) {
					strRet.append("Ret: " + To_Hex(cData, intAPDU_len[0]));

				} else {
					strRet.append(strErr + Integer.toHexString(cRet) + "\n");
				}

				updateStatus(strRet);
			}
		});
		// Send Command and Get Response(Access Mode)
		access.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				sTemp = String.valueOf(EditTextCMD.getText());
				for (int i = 0; i < sTemp.length(); i = i + 2) {
					cCmd = sTemp.substring(i, i + 2).toCharArray();
					if (StrToHex(cCmd) == true) {
						strCMD += new Character((char) giReturnValue)
								.toString();
					}
				}

				cRet = SDLib.InCOMM_SmartSDCard_AccessAPDUCommand(
						strCMD.toCharArray().length, strCMD.toCharArray(),
						intAPDU_len, cData, 0);
				if (cRet == 0x00) {
					strRet.append("Send CMD: " + sTemp + "\nRet:"
							+ To_Hex(cData, intAPDU_len[0]));

				} else {
					strRet.append(strErr + Integer.toHexString(cRet) + "\n");
				}

				updateStatus(strRet);
			}
		});
		// Set PPS
		setpps.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				cRet = SDLib.InCOMM_SmartSDCard_Reset(intAPDU_len, cData);
				if (cRet == 0x00)
					strRet.append("Reset: " + To_Hex(cData, intAPDU_len[0])
							+ "\n");
				else {
					strRet.append("Reset FAIL!!\n" + strErr
							+ Integer.toHexString(cRet) + "\n");

					updateStatus(strRet);
					return;
				}

				sTemp = String.valueOf(EditTextCMD.getText());
				for (int i = 0; i < sTemp.length(); i = i + 2) {
					cCmd = sTemp.substring(i, i + 2).toCharArray();
					if (StrToHex(cCmd) == true) {
						strCMD += new Character((char) giReturnValue)
								.toString();
					}
				}

				iPPSIdx = Integer.parseInt(EditTextIndex.getText().toString());

				cRet = SDLib.InCOMM_SmartSDCard_SendPPSCommand(
						strCMD.toCharArray().length, strCMD.toCharArray(),
						iPPSIdx);
				if (cRet != 0x00) {
					strRet.append("SETPPS: " + strErr
							+ Integer.toHexString(cRet));
					updateStatus(strRet);
					return;
				}

				try {
					Thread.currentThread();
					Thread.sleep(500);
				} catch (Exception e) {
				}

				cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
						cData);

				if (cRet == 0x00) {
					strRet.append("Set PPS Ret: "
							+ To_Hex(cData, intAPDU_len[0]) + "\n");

					updateStatus(strRet);
					return;
				} else {
					strRet.append("Set PPS FAIL!!\n" + strErr
							+ Integer.toHexString(cRet) + "\n");
					updateStatus(strRet);
					return;
				}
			}
		});
		// Clear Message on TextView
		clear.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());
				updateStatus(strRet);
			}
		});
		// Set Handle 61 Response On or Off.
		handle61.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				if (intOnOff == 0) {
					intOnOff = 1;
					cRet = SDLib
							.InCOMM_SmartSDCard_SetupAPDUReturnStatus(intOnOff);
					if (cRet == 0x00) {
						strRet.append("Handle 61response set On!! \n");
					} else {
						strRet.append("Handle 61response set On Fail!!\n"
								+ strErr + Integer.toHexString(cRet) + "\n");
					}
				} else {
					intOnOff = 0;
					cRet = SDLib
							.InCOMM_SmartSDCard_SetupAPDUReturnStatus(intOnOff);
					if (cRet == 0x00) {
						strRet.append("Handle 61response set Off!! \n");
					} else {
						strRet.append("Handle 61response set Off Fail!!\n"
								+ strErr + Integer.toHexString(cRet) + "\n");
					}
				}

				updateStatus(strRet);
			}
		});
		// Enable Fixed Data Length Command
		enFixLen.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				String sTemp = "";
				String strCMD = "";
				char cCmd[];

				sTemp = String.valueOf(EditTextCMD.getText());
				for (int i = 0; i < sTemp.length(); i = i + 2) {
					cCmd = sTemp.substring(i, i + 2).toCharArray();
					if (StrToHex(cCmd) == true) {
						strCMD += new Character((char) giReturnValue)
								.toString();
					}
				}
				cIndex = (char) Integer.parseInt(EditTextIndex.getText()
						.toString());

				// cRet = SDLib.InCOMM_SmartSDCard_EnableFixedDataLengthCommand(
				// strCMD.toCharArray().length, strCMD.toCharArray(), cIndex);
				cRet = SDLib.InCOMM_SmartSDCard_EnableFixedDataLengthCommand(
						SPI_CMD_500.length, SPI_CMD_500, cIndex);
				if (cRet == 0x00) {
					strRet.append("Enable Fixed Data Length OK( "
							+ String.valueOf(cIndex * 500) + " Bytes)\n");
				} else {
					strRet.append("Enable Fixed Data Length Fail( "
							+ String.valueOf(cIndex * 500) + " Bytes)\n"
							+ strErr + Integer.toHexString(cRet) + "\n");
				}
				updateStatus(strRet);
			}
		});
		// Send Data
		sendData.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());
				cIndex = (char) Integer.parseInt(EditTextIndex.getText()
						.toString());
				char cSendData[] = new char[cIndex * 500 + 12];

				memset(cSendData, (char) (Math.random() * 256),
						cSendData.length);

				cRet = SDLib.InCOMM_SmartSDCard_SendData(cSendData);
				if (cRet != 0x00) {
					strRet.append("Send Data Err " + Integer.toHexString(cRet)
							+ "\n");
				} else {
					strRet.append("Send Data: ( "
							+ String.valueOf(cIndex * 500) + " Bytes)\n");
					strRet.append(To_Hex(cSendData, cIndex * 500) + "\n");
				}

				cSendData = null;
				updateStatus(strRet);
			}
		});
		// Get Data
		getData.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				char cGetData[] = new char[cIndex * 500 + 12];

				cRet = SDLib.InCOMM_SmartSDCard_GetData(cGetData);
				if (cRet != 0x00) {
					strRet.append("Get Data Err " + Integer.toHexString(cRet)
							+ "\n");
				} else {
					strRet.append("Get Data: ( " + String.valueOf(cIndex * 500)
							+ " Bytes)\n");
					strRet.append(To_Hex(cGetData, cIndex * 500) + "\n");
				}

				cGetData = null;
				updateStatus(strRet);
			}
		});
		// Calculate transmission speed
		speed.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				double T1 = 0, T2 = 0, T3 = 0;
				double dbps = 0;
				int loopTimes = CASE4_TEST_BLOCK_NUM / FixedDataLength;
				int i;

				for (i = 0; i < CASE4_FIX_DATA_LEN * FixedDataLength; i++)
					inbuf[i] = (char) (Math.random() * 256);

				// cRet = SDLib.InCOMM_SmartSDCard_EnableFixedDataCommand(5,
				// SPI_CMD_500);
				// SPI_CMD_500[3] = FixedDataLength;
				cRet = SDLib.InCOMM_SmartSDCard_EnableFixedDataLengthCommand(5,
						SPI_CMD_500, FixedDataLength);
				cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
						cData);

				T1 = System.currentTimeMillis();
				for (int iloop = 0; iloop < loopTimes; iloop++) {
					cRet = SDLib.InCOMM_SmartSDCard_SendData(inbuf);
					do {
						cRet = SDLib.InCOMM_SmartSDCard_GetData(outbuf);
						strRet.append(" Speed get data ret NO." + (iloop + 1)
								+ " = " + Integer.toHexString(cRet) + "\n");
					} while (memcmp(outbuf, ITRUST, 6) == 0);
					// for(i=0; i<CASE4_FIX_DATA_LEN*FixedDataLength; i++)
					// if(outbuf[i]!=inbuf[i]) {
					// strRet.append("DataError\n");
					// break;
					// }
				}

				T2 = System.currentTimeMillis();
				T3 = (T2 - T1);// /1000;

				cRet = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(5, SPI_END_500);
				cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
						cData);

				dbps = CASE4_FIX_DATA_LEN * FixedDataLength * 8 * 2 * loopTimes
						/ T3;

				strRet.append("R/W :" + String.valueOf(loopTimes) + " times"
						+ "\n");
				strRet.append("Total used time: " + String.valueOf((double) T3)
						+ " ms\n");
				strRet.append(String.valueOf(dbps) + " Kbps " + "\n");

				updateStatus(strRet);
			}
		});
		// Run App
		runapp.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());

				char cSendData[] = { 0xAA, 0xE3, 0x04, 0x00, 0x00, 0x00, 0x00,
						0x55 };

				cRet = SDLib.InCOMM_SmartSDCard_Reset(intAPDU_len, cData);
				if (cRet == 0x00)
					strRet.append("Reset OK!!\n");
				else {
					strRet.append("Reset FAIL!!\n");
					updateStatus(strRet);
					cSendData = null;
					return;
				}

				cRet = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(8, cSendData);
				if (cRet == 0x00)
					strRet.append("Send Cmd OK!!\n");
				else {
					strRet.append("Send Cmd Faill!! Err "
							+ Integer.toHexString(cRet) + "\n");
					updateStatus(strRet);
					cSendData = null;
					return;
				}

				cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
						cData);
				if (cRet == 0x00)
					strRet.append("Run App OK!!\n"
							+ To_Hex(cData, 0, intAPDU_len[0]));
				else {
					strRet.append("Run App FAIL!! Err "
							+ Integer.toHexString(cRet) + "\n");
					updateStatus(strRet);
					cSendData = null;
					return;
				}

				updateStatus(strRet);
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/* Android 作为Server时使用以下代码 */
				/**********************************/
				strRet.delete(0, strRet.length());
				try {
					// 创建服务端ServerSocket对象
					ServerSocket serverSocket = new ServerSocket(8001);

					// ServerSocket.accept()方法用于等待客服连接
					while (true) {
						Socket socket = serverSocket.accept();

						// 获取客服端数据
						byte[] cSendData = new byte[512];
						socket.getInputStream().read(cSendData);
						String s = bytesToHexString(cSendData);

						char[] c = new char[512];
						for (int i = 0; i < cSendData.length; i++)
							c[i] = (char) (0xFF & cSendData[i]);

						// 加密模块
						cIndex = (char) Integer.parseInt(EditTextIndex
								.getText().toString());
						cRet = SDLib
								.InCOMM_SmartSDCard_EnableFixedDataLengthCommand(
										5, SPI_CMD_500_E, cIndex);
						cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(
								intAPDU_len, cData);

						cRet = SDLib.InCOMM_SmartSDCard_SendData(c);
						strRet.append("cretsend:" + Integer.toHexString(cRet)
								+ "\n");

						char[] cGetData = new char[512];
						int counter = 0;
						do {
							counter++;
							cRet = SDLib.InCOMM_SmartSDCard_GetData(cGetData);
							strRet.append("cretget:"
									+ Integer.toHexString(cRet) + "\n");
						} while (memcmp(outbuf, ITRUST, 6) == 0);

						strRet.append("Loop times: " + counter + "\n");

						cRet = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(5,
								SPI_END_500_E);
						cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(
								intAPDU_len, cData);
						// char cSendData[] =
						// bufferedReader.readLine().toCharArray();
						// cSendData = bufferedReader.readLine().toCharArray();

						strRet.append("Recieve Message:"
								+ To_Hex(cGetData, 512) + "\n");
						updateStatus(strRet);

						// 关闭服务连接
						serverSocket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				/*********************************************/

				/* Android 作为Client时使用以下代码 */
				/**********************************/
				/*
				 * try { //创建Socket连接对象 （ip地址，端口）
				 * updateStatus("try ok"); Socket socket = new
				 * Socket("192.168.1.193",8001); //PrintWriter发送对象
				 * updateStatus("new sock ok"); PrintWriter printWriter = new
				 * PrintWriter(new
				 * OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				 * //向服务器发送信息
				 * printWriter.println(EditTextCMD.getText());
				 * 
				 * updateStatus("send ok");
				 * 
				 * printWriter.close(); //bufferedReader.close();
				 * socket.close(); } catch (UnknownHostException e) {
				 * e.printStackTrace(); } catch (IOException e) {
				 * e.printStackTrace(); }
				 * 
				 * /****************************************
				 */
			}
		});

		smsSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strRet.delete(0, strRet.length());
				try {
					byte[] cSendData = new byte[512];
					byte[] smsbody = smsBodyEditText.getText().toString()
							.getBytes();
					for (int i = 0; i < (smsbody.length < cSendData.length ? smsbody.length
							: cSendData.length); i++) {
						cSendData[i] = smsbody[i];
					}

					char[] c = new char[512];
					for (int i = 0; i < cSendData.length; i++)
						c[i] = (char) (0xFF & cSendData[i]);

					cIndex = (char) Integer.parseInt(EditTextIndex.getText()
							.toString());
					cRet = SDLib
							.InCOMM_SmartSDCard_EnableFixedDataLengthCommand(5,
									SPI_CMD_500_E, cIndex);
					cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
							cData);

					cRet = SDLib.InCOMM_SmartSDCard_SendData(c);
					strRet.append("cretsend:" + Integer.toHexString(cRet)
							+ "\n");

					char[] cGetData = new char[512];
					int counter = 0;
					do {
						counter++;
						cRet = SDLib.InCOMM_SmartSDCard_GetData(cGetData);
						strRet.append("cretget:" + Integer.toHexString(cRet)
								+ "\n");
					} while (memcmp(outbuf, ITRUST, 6) == 0);

					strRet.append("Loop times: " + counter + "\n");

					cRet = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(5,
							SPI_END_500_E);
					cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len,
							cData);
					// char cSendData[] =
					// bufferedReader.readLine().toCharArray();
					// cSendData = bufferedReader.readLine().toCharArray();

					String smsBody = To_Hex(cGetData, 512);
					String smsNumber = smsToEditText.getText().toString();

					strRet.append("To send Message:" + smsBody + "\n");
					Log.d("SDM", "Ori:" + smsBodyEditText.getText().toString());
					Log.d("SDM", "Encrypted:" + smsBody);
					// Log.d("SDM", strRet.toString());
					// SmsManager sm = SmsManager.getDefault();
					// sm.sendTextMessage(smsNumber, null, smsBody, null, null);

					// do decrypt here..have a try..
					byte[] toDe = new byte[512];
					for (int i = 0; i < 512; i++) {
						toDe[i] = (byte) (0xff & cGetData[i]);
					}
					String de = decryptData(toDe);

					strRet.append("Str after decrypt:" + de + "\n");
					Log.d("SDM", "Decrypted:" + de);

					updateStatus(strRet);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void updateStatus(StringBuffer strStatus) {
		showStatus = strStatus.toString();
		showInfoHandler.sendEmptyMessage(iUpdateStatusFlag);
	}

	public static void addSmsMessage(String smsFrom, String smsBody) {
		if (instance != null) {
			String body = instance.decryptMessage(smsBody);
			instance.doAddSmsMesssage(smsFrom, body);
		}
	}

	private String decryptMessage(String message) {
		// TODO
		return null;
	}

	private String decryptData(byte[] smsbody) {
		strRet.delete(0, strRet.length());
		try {
			byte[] cSendData = new byte[512];
			for (int i = 0; i < (smsbody.length < cSendData.length ? smsbody.length
					: cSendData.length); i++) {
				cSendData[i] = smsbody[i];
			}

			char[] c = new char[512];
			for (int i = 0; i < cSendData.length; i++)
				c[i] = (char) (0xFF & cSendData[i]);

			cIndex = (char) Integer
					.parseInt(EditTextIndex.getText().toString());
			cRet = SDLib.InCOMM_SmartSDCard_EnableFixedDataLengthCommand(5,
					SPI_CMD_500, cIndex);
			cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len, cData);

			cRet = SDLib.InCOMM_SmartSDCard_SendData(c);
			strRet.append("cretsend:" + Integer.toHexString(cRet) + "\n");

			char[] cGetData = new char[512];
			int counter = 0;
			do {
				counter++;
				cRet = SDLib.InCOMM_SmartSDCard_GetData(cGetData);
				strRet.append("cretget:" + Integer.toHexString(cRet) + "\n");
			} while (memcmp(outbuf, ITRUST, 6) == 0);

			strRet.append("Loop times: " + counter + "\n");

			cRet = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(5, SPI_END_500);
			cRet = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(intAPDU_len, cData);

			//String smsBody = To_Hex(cGetData, 512);
			String smsBody = new String(cGetData);
			//Log.d("SDM", sms)
			strRet.append("To send Message:" + smsBody + "\n");

			updateStatus(strRet);
			return smsBody;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void doAddSmsMesssage(String smsFrom, String smsBody) {
		showStatus = smsFrom + " | " + smsBody;
		showInfoHandler.sendEmptyMessage(iUpdateStatusFlag);
	}

	private boolean StrToHex(char StrHex[]) {
		int tempVlaue;
		if (HexStrtInt(StrHex[1]) == true)
			tempVlaue = giReturnValue;
		else
			return false;
		if (HexStrtInt(StrHex[0]) == true)
			giReturnValue = giReturnValue * 16 + tempVlaue;
		else
			return false;
		return true;

	}

	private boolean HexStrtInt(char charvalue) {
		switch (charvalue) {
		case '0':
			giReturnValue = 0;
			break;
		case '1':
			giReturnValue = 1;
			break;
		case '2':
			giReturnValue = 2;
			break;
		case '3':
			giReturnValue = 3;
			break;
		case '4':
			giReturnValue = 4;
			break;
		case '5':
			giReturnValue = 5;
			break;
		case '6':
			giReturnValue = 6;
			break;
		case '7':
			giReturnValue = 7;
			break;
		case '8':
			giReturnValue = 8;
			break;
		case '9':
			giReturnValue = 9;
			break;
		case 'a':
		case 'A':
			giReturnValue = 10;
			break;
		case 'b':
		case 'B':
			giReturnValue = 11;
			break;
		case 'c':
		case 'C':
			giReturnValue = 12;
			break;
		case 'd':
		case 'D':
			giReturnValue = 13;
			break;
		case 'e':
		case 'E':
			giReturnValue = 14;
			break;
		case 'f':
		case 'F':
			giReturnValue = 15;
			break;
		default:
			return false;
		}
		return true;
	}

	private String To_Hex(char cData[], int nlen) { // TODO : To_Hex
		char[] finalhash = new char[nlen * 2];
		char[] hexval = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		for (int j = 0; j < nlen; j++) {
			finalhash[j * 2] = hexval[(int) ((cData[j] >> 4) & 0xF)];
			finalhash[(j * 2) + 1] = hexval[(int) ((cData[j]) & 0x0F)];
		}
		return String.valueOf(finalhash);
	}

	private String To_Hex(char cData[], int offset, int nlen) { // TODO : To_Hex
		int nSize = (nlen - offset);
		char[] finalhash = new char[nSize * 2];
		char[] hexval = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		for (int j = 0; j < nSize; j++) {
			finalhash[j * 2] = hexval[(int) ((cData[j + offset] >> 4) & 0xF)];
			finalhash[(j * 2) + 1] = hexval[(int) (cData[j + offset]) & 0x0F];
		}

		return String.valueOf(finalhash);
	}

	private void memset(char cData[], int set_Val, int set_Len) {
		for (int i = 0; i < set_Len; i++) {
			cData[i] = (char) set_Val;
		}
	}

	private char memcmp(char src1[], char src2[], int len) {
		for (int i = 0; i < len; i++) {
			if (src1[i] != src2[i])
				return 1;
		}
		return 0;
	}

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/*
	 * private char SmartSD_Case4_Trans(char pCMD1[], int cLen1, char pRES1[],
	 * char pIn[][], char pOut[][], int count, char pCMD2[], int cLen2, char
	 * pRES2[]) { char u8Ret; char ITRUST[] = {'I','T','R','U','S','T'}; int
	 * nLen[] = new int[1]; int retry = 0; int i;
	 * 
	 * u8Ret = SDLib.InCOMM_SmartSDCard_EnableFixedDataCommand(cLen1, pCMD1);
	 * if(u8Ret!=0x00) { return u8Ret; }
	 * 
	 * do { u8Ret = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(nLen, pRES1);
	 * if(u8Ret!=0x00) { return u8Ret; } }while((nLen[0] == 0) && ((retry++) <
	 * SMARTSD_MAX_RETRY_NUM));
	 * 
	 * 
	 * for(i=0; i<count; i++) { u8Ret =
	 * SDLib.InCOMM_SmartSDCard_SendData(pIn[i]); if(u8Ret!=0x00) { return
	 * u8Ret; }
	 * 
	 * do { u8Ret = SDLib.InCOMM_SmartSDCard_GetData(pOut[i]); if(u8Ret!=0x00) {
	 * return u8Ret; } }while((memcmp(pOut[i],ITRUST,6) == 0) && ((retry++) <
	 * SMARTSD_MAX_RETRY_NUM)); // Wait for Data Ready }
	 * 
	 * u8Ret = SDLib.InCOMM_SmartSDCard_SendAPDUCommand(cLen2, pCMD2);
	 * if(u8Ret!=0x00) { return u8Ret; }
	 * 
	 * do { u8Ret = SDLib.InCOMM_SmartSDCard_GetAPDUCommand(nLen, pRES2);
	 * if(u8Ret!=0x00) { return u8Ret; } }while((nLen[0] == 0) && ((retry++) <
	 * SMARTSD_MAX_RETRY_NUM));
	 * 
	 * return 0; }
	 */
}
