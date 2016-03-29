package com.ta.bean;

import com.ta.resource.Globals;

public class ServerTest {

	public static void main(String[] args) {
		RunNanoServer rns=new RunNanoServer();
		RWProperties rwp=new RWProperties();
		rns.doNanoServerStart_Unix(rwp.getPropValues(Globals._NANO_COMMAND_PATH), rwp.getPropValues(Globals._NANO_LOG_PATH));
		//SampleBlazegraphSesameRemote sbr=new SampleBlazegraphSesameRemote();
	}

}
