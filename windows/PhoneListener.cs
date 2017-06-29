using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Threading;

namespace Networking
{
    class PhoneListener
    {

        public static string data;
        static TcpListener tcpListener;
        ProcessHandler phObj;

        /**
         * Constructor
         * initializes connection between phone and computer
         * creates socket to listen for requests from phone
        **/
        public PhoneListener(IPAddress ip_addr, ProcessHandler phObj)
        {            
            this.phObj = phObj;
            sendInitialBroadcast();
            tcpListener = new TcpListener(ip_addr, 65000);
            tcpListener.Start();
            Console.WriteLine("Server listening to port 65000");
        }

        /**
         * Description:
         * Sends UDP broadcast to phone to initialize connection
         * creates socket for broadcast to initially communicate local machine's IPAddress
         * closes socket upon completion
        **/
        private void sendInitialBroadcast()
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
                Console.WriteLine("Success");
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
            Console.WriteLine("Waiting for request from Phone...");
            Socket handler = tcpListener.AcceptSocket();
            Console.WriteLine("Recieved connection from phone at {0}", handler.RemoteEndPoint);
            try
            {
                data = null;
                Stream s = new NetworkStream(handler);
                StreamWriter sw = new StreamWriter(s);
                StreamReader sr = new StreamReader(s);
                sw.AutoFlush = true;
                while (true)
                {
                    try
                    {
                        data = sr.ReadLine();
                        Console.WriteLine(data);
                        if (data.IndexOf("<EC>") > -1)
                        {
                            sw.WriteLine("shut the fuck up");
                        }
                    } catch (SocketException e)
                    {
                        Console.WriteLine("Error occured");
                        Console.WriteLine(e.ToString());
                        break;
                    }
                    catch(NullReferenceException e)
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
            } catch (Exception e)
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

