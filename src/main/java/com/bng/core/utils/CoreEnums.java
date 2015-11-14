/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.utils;

/**
 *
 * @author richa
 */
public class CoreEnums {
    public static enum SCPTypeEnum{
        Start,
        Answer,
        Dialout,
        Timer,
        Database,
        Navigation,
        Processing,        
        Play,
        PlayStreaming,
        URL,
        DigitCollection,
        RecordDedication,
        StopRecording,
        Exit,
        Live,
        Patch,
        UnPatch,
        Wait,
        MagicParrot,
        Goto,
        Randomizer,
        Validation,
        Vxml, 
        DynamicPlay, 
        Multiplay,
        Probability;
        public static final SCPTypeEnum values[] = values();
    }
    
    public static enum callOperations{
    	pickup,
        hangup,
        alert;
    	public static final callOperations values[] = values();
    }
    
    public static enum events{
        DEFAULT,
        E_ChState,
        E_MakeCall,
        E_DTMF,
        E_PlayingFile,
        E_PlayListEOF,
        E_PlayEnd,
        E_PlayMem,
        E_RecordingFIle,
        E_RecordEnd,
        E_WaitDTMF,
        E_OBD,
        E_GenerateDTMF,	///@gp
        E_VXML;	//13
        public static final events values[] = values();
    }
    
    public static enum subEvents{
    	DEFAULT,
        S_CallRinging,
        S_CallStandby,
        S_CallPending,
        S_CallTalking,
        S_CallDetected,
        MasterTimerExp,
        RecordTimerExp,
        FreeMinTimerExp,
    	SampleCheckTimerExp,
    	CallPatched,
    	DoNothing,
    	BPartyCallFailed,	//12
    	CallPatchedWithCG,
    	DialoutTimerExp,
    	BpartyHangupContinueWithAparty,		//15
    	UserDefinedTimerExp,
    	BpartyTalkingReceived,
    	Vxmlcontrolstart, //18
    	vxmlcontrolover, //19
    	vxmlplay, 
    	AlreadyPicked;  
    	public static final subEvents values[] = values();
    }
    
    public static enum makeCallSubEvent{
    	DEFAULT,
    	S_OutGoingFailed,
    	S_OutGoingOffHook,
    	S_OutGoingRinging;
    	public static final makeCallSubEvent values[] = values();
    }
    
    public static enum subEventCause{        	
    	RemoteHangupFirst,
    	RLCSent,
    	NetworkBusy,
    	ReleasingCall,
    	NoAnswer,
    	CallRejection,
    	Unavailable,
    	SwitchedOff,
    	UserAbsent,
    	NumberFormatInvalid,
    	CircuitChannelUnavailable,
    	Misc,
    	E1Fluctuation;
    	public static final subEventCause values[] = values();
    }
    public static enum playEndSubEvent{
    	DEFAULT,
    	S_PlayComplete,
    	S_PlayTerminatedOnDtmf,
    	S_PlayTerminatedOnBargein,
    	S_PlayTerminatedOnRemoteHangup,
    	S_PlayStopByApp,
    	S_PlayPaused,
    	S_PlayTerminatedBusOp,
    	S_PlayTerminateNetworkFault;
    	public static final playEndSubEvent values[] = values();
    }
    
    public static enum hardware{
        DEFAULT,
        Synway,
        Dialogic,
        SIP,
        XYZ;
        public static final hardware values[] = values();
    }
    
    public static enum callType{
        Incoming,
        Outgoing,
        Alert;
        public static final callType values[] = values();
    }    
    
    public static enum playOperations{
        play,
        stop;
        public static final playOperations values[] = values();
    }
    
    public static enum recordOperations{
        record,
        end;
        public static final recordOperations values[] = values();
    }
    
    public static enum cli{
    	aPartyMsisdn,
    	shortCode,
    	starChat;
    	public static final cli values[] = values();
    }
    
    public static enum patchOperations{
    	Patch,
    	Unpatch,
    	Release;
    	public static final patchOperations values[] = values();
    }
    
    public static enum magicParrotOper{
    	StartMP,
    	StopMP;
    	public static final magicParrotOper values[] = values();
    }
}
