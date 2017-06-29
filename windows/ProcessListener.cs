using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Management;
using System.Timers;
using System.Diagnostics;

namespace Networking
{
    /**
     * Process Listener class to monitor open processes by polling
     * Notes
     * -currently seperated into own class in case more efficient way is found
     * -uses ElapsedEventHandlers
    **/

    class ProcessListener
    {
        //Timer to be used to control flow of polling
        private Timer pollTimer;
        private ProcessHandler pHandler;

        public ProcessListener(ProcessHandler pHandler)
        {
            this.pHandler = pHandler;
            pollTimer = new Timer(2000); //set polling rate to .5 Hz (every 2 seconds)
            pollTimer.Enabled = true;
            pollTimer.Elapsed += new ElapsedEventHandler(pHandler.pollProcessList); //add invoked (to-be) method to event handler
            pollTimer.Start(); //start timer
        }

        /**
         * Empty for now 
         * Used just to keep code running and not make Timer obsolete
        **/
        public void run()
        {
            while (true)
            {

            }
        }
    }
}
