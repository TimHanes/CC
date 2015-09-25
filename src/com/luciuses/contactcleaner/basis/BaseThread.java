package com.luciuses.contactcleaner.basis;

	public class BaseThread extends Thread {
		
		boolean suspended = false;
		
		private Thread m_thread;
		Object autoEvent;

		public BaseThread() {			
			m_thread = new Thread(this);
		}

		public Thread.State IsThreadState() {			
				return m_thread.getState();			
		}

		public void Start() {
			m_thread.start();
		}

		public void Pause() {			
			      suspended = true;			   
		}

		public synchronized void Resume() {
		      suspended = false;
		       notify();
		   }

		@Override
		public void run()
		{
			synchronized(this) {
            while(suspended) {
                try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
			}
		}
	}


