package Incomm.Library;

public class SmartSDLib {
	
	public SmartSDLib(){
	      System.loadLibrary("SPISmartSDLib");
	    }
	
	    public native char InCOMM_SmartSDCard_Initialization(String strPath);
	    public native char InCOMM_SmartSDCard_Reset(int ATR_len[],char[] charATR_Buffer);
	    public native char InCOMM_SmartSDCard_GetCardVersion(char[] card_version);
	    public native char InCOMM_SmartSDCard_GetCID(char[] card_cid);
	    public native char InCOMM_SmartSDCard_SetupAPDUReturnStatus(int intOnOff);
	    public native char InCOMM_SmartSDCard_Format();
	    public native void InCOMM_SmartSDCard_GetDLLVersion(char[] dll_version);
	    public native char InCOMM_SmartSDCard_EnableFixedDataCommand(int intAPDU_len, char[] charAPDU_Buffer);
	    public native char InCOMM_SmartSDCard_EnableFixedDataLengthCommand(int intAPDU_len, char[] charAPDU_Buffer, char charSendDataSize);
	    public native char InCOMM_SmartSDCard_SendAPDUCommand(int intAPDU_len, char[] charAPDU_Buffer);
	    public native char InCOMM_SmartSDCard_GetAPDUCommand(int intAPDU_len[], char[] charAPDU_Buffer);
	    public native char InCOMM_SmartSDCard_GetAPDUCommand_Block(int intAPDU_len[], char[] charAPDU_Buffer, int intWaiting_time);
	    public native char InCOMM_SmartSDCard_AccessAPDUCommand(int intSendAPDU_len, char[] charSendAPDU_Buffer, int intGetAPDU_len[], char[] charGetAPDU_Buffer, int intWaiting_time);
	    public native char InCOMM_SmartSDCard_SendData(char[] charAPDU_Buffer);
	    public native char InCOMM_SmartSDCard_GetData (char[] charAPDU_Buffer);
	    public native char InCOMM_SmartSDCard_Endtransmission();
	    public native char InCOMM_SmartSDCard_SendPPSCommand(int intAPDU_len, char[] charAPDU_Buffer,int intPPSIdx);
	    public native char InCOMM_SmartSDCard_SetSPICLK(int intIndex);
	    public native char In_SmartSDCard_Initialization(String strPath);
	    public native char In_SmartSDCard_Reset(int ATR_len[],char[] charATR_Buffer);
	    public native char In_SmartSDCard_GetCardVersion(char[] card_version);
	    public native char In_SmartSDCard_GetCID(char[] card_cid);
	    public native char In_SmartSDCard_SetupAPDUReturnStatus(int intOnOff);
	    public native char In_SmartSDCard_Format();
	    public native void In_SmartSDCard_GetDLLVersion(char[] dll_version);
	    public native char In_SmartSDCard_EnableFixedDataCommand(int intAPDU_len, char[] charAPDU_Buffer);
	    public native char In_SmartSDCard_EnableFixedDataLengthCommand(int intAPDU_len, char[] charAPDU_Buffer, char charSendDataSize);
	    public native char In_SmartSDCard_SendAPDUCommand(int intAPDU_len, char[] charAPDU_Buffer);
	    public native char In_SmartSDCard_GetAPDUCommand(int intAPDU_len[], char[] charAPDU_Buffer);
	    public native char In_SmartSDCard_GetAPDUCommand_Block(int intAPDU_len[], char[] charAPDU_Buffer, int intWaiting_time);
	    public native char In_SmartSDCard_AccessAPDUCommand(int intSendAPDU_len, char[] charSendAPDU_Buffer, int intGetAPDU_len[], char[] charGetAPDU_Buffer, int intWaiting_time);
	    public native char In_SmartSDCard_SendData(char[] charAPDU_Buffer);
	    public native char In_SmartSDCard_GetData (char[] charAPDU_Buffer);
	    public native char In_SmartSDCard_Endtransmission();
	    public native char In_SmartSDCard_SendPPSCommand(int intAPDU_len, char[] charAPDU_Buffer,int intPPSIdx);
	    public native char In_SmartSDCard_SetSPICLK(int intIndex);
}
