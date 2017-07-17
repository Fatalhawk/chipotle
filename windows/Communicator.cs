/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

using System;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.IO;

namespace Networking
{
    /**
     * Static class used to communicate with device
     * static because program should only have one communication point between phone and computer
     * ensures no conflicts in sending or recieving messages concurrently     * 
     **/
    static class Communicator
    {

        public static string data;
        static TcpListener tcpListener;
        static Stream s;
        static StreamWriter sw;
        static StreamReader sr;
        public delegate void commandRecieved(string cmd);
        public static commandRecieved sendCommand;

        /**
         * Constructor
         * initializes connection between phone and computer
         * creates socket to listen for requests from phone
        **/
        static Communicator()
        {
            sendInitialBroadcast();
            tcpListener = initListener();
            tcpListener.Start();
        }

        /**
         * initializes a TcpListener by obtaining local IP address and port
         * @return: main TcpListener to foster communications
         **/
        private static TcpListener initListener()
        {
            //default loop-back IP
            IPAddress local_IP = IPAddress.Parse("127.0.0.1");

            //look through DNS to find local IP address
            foreach (var addr in Dns.GetHostEntry(string.Empty).AddressList)
            {
                if (addr.AddressFamily == AddressFamily.InterNetwork)
                {
                    local_IP = addr;
                }
                //Console.WriteLine(addr.ToString());
            }

            return new TcpListener(local_IP, 65000);
        }

        /**
         * Description:
         * Sends UDP broadcast to phone to initialize connection
         * creates socket for broadcast to initially communicate local machine's IPAddress
         * closes socket upon completion
        **/
        private static void sendInitialBroadcast()
        {
            //Create socket to send out signal 
            Socket s = new Socket(AddressFamily.InterNetwork, SocketType.Dgram,
            ProtocolType.Udp);
            s.EnableBroadcast = true;

            //instantiate endpoint IP address for broadcast
            IPAddress broadcast = IPAddress.Parse(IPAddress.Broadcast.ToString());

            //create message
            byte[] sendbuf = Encoding.ASCII.GetBytes("test message");

            //instantiates endpoint address plus port
            IPEndPoint ep = new IPEndPoint(broadcast, 4444);


            try
            {
                s.SendTo(sendbuf, ep);
                //Console.WriteLine("Success");
            }
            catch (SocketException e)
            {
                Console.WriteLine(e.ToString());
            }
            finally
            {
                s.Close();
            }
        }

        /**
         * Description:
         * establishes thread between computer and phone
         * Connects to one endpoint at a time and constantly listens for requests from endpoint
        **/
        public static void listenForRequests()
        {
            sendCommand("Waiting for request from Phone...");
            sendCommand("Waiting for requests from Phone...");
            Socket handler = tcpListener.AcceptSocket();
            string rCfP = string.Format("Recieved connection from phone at {0}", handler.RemoteEndPoint);
            sendCommand(rCfP);//"Recieved connection from phone at {0}", handler.RemoteEndPoint);
            try
            {
                data = null;
                s = new NetworkStream(handler);
                sw = new StreamWriter(s);
                sr = new StreamReader(s);
                sw.AutoFlush = true;
                while (true)
                {
                    try
                    {
                        data = sr.ReadLine();
                        Console.WriteLine(data);
                        if (data.IndexOf("<EC>") > -1)
                        {
                            sendCommand(data);
                            sw.WriteLine("shut the fuck up");
                        }
                    }
                    catch (SocketException e)
                    {
                        //Console.WriteLine("Error occured");
                        //Console.WriteLine(e.ToString());
                        break;
                    }
                    catch (NullReferenceException e)
                    {
                        Console.WriteLine("Socket closed or not available. Establishing new thread...");
                        handler = tcpListener.AcceptSocket();
                        s.Close();
                        s = new NetworkStream(handler);
                        sw = new StreamWriter(s);
                        sr = new StreamReader(s);
                        sw.AutoFlush = true;
                        //Console.WriteLine(e.ToString());
                        continue;
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.ToString());
                        break;
                    }

                }
                s.Close();
                Console.WriteLine("Exiting Listen");
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
            finally
            {
                handler.Close();
                Console.WriteLine("Exiting Program");
            }
        }
    }
}
