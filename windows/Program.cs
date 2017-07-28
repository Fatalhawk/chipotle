using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;
/**
* Author(s): Takahiro Tow
* Last updated: July 6, 2017
**/
namespace Networking
{
    class Program
    {
        static void Main(string[] args)
        {
            TestGUI tGUI = new TestGUI();
            ProcessListener pListener = new ProcessListener(tGUI);
            //tGUI.updateGridView(ProcessHandler.getProcessList());
            //ProcessHandler.setProcessList(pListener.updateProcessList());
            //Thread t = new Thread(Communicator.listenForRequests);
            //t.Start();
            Application.Run(tGUI);
            //Initializes ProcessHandler (main flow control of program)
            //ProcessHandler.printProcessList();   
        }
    }
}
