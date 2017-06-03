package com.pigcms.library.android.okhttp.callback;


public interface ResponseCallback {
	
	 void OnSuccess(String classinfo);
	
	 void OnFail(String arg0, String arg1);


	//未拓展
	/*public  void OnStart();*/
	//未拓展
	 void OnFinsh();

}
