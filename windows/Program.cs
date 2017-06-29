using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Management;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace Networking
{    
    class Program
    {
        static void Main(string[] args)
        {
            ProcessHandler processHandler = new ProcessHandler();
            processHandler.printProcessList();
            ProcessListener processListener = new ProcessListener(processHandler);
            Thread s = new Thread(processListener.run);
            s.Start();
            

            //default loop-back IP
            IPAddress local_IP = IPAddress.Parse("127.0.0.1");

            //look through DNS to find local IP address
            foreach (var addr in Dns.GetHostEntry(string.Empty).AddressList)
            {
                if (addr.AddressFamily == AddressFamily.InterNetwork) {
                    local_IP = addr;
                }
                //Console.WriteLine(addr.ToString());
            }

            //PhoneListener pListener = new PhoneListener(local_IP, processHandler);
            //Thread t = new Thread(PhoneListener.listenForRequests);
            //t.Start();
        }
    }
}
