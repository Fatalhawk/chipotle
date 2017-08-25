/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

using System;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Drawing;

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
        static Socket handler;
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
            tcpListener = initListener();
            sendInitialBroadcast();
            tcpListener.Start();
            //handler = tcpListener.AcceptSocket();
            //s = new NetworkStream(handler);
            //sw = new StreamWriter(s);
            //sr = new StreamReader(s);
            //sw.AutoFlush = true;
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

        public static void sendMessage(int cmdTarget, int cmd)
        {

        }

        /**
         * Description:
         * establishes thread between computer and phone
         * Connects to one endpoint at a time and constantly listens for requests from endpoint
        **/
        public static void listenForRequests()
        {
            try
            {
                data = null;
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
                        Console.WriteLine(e.ToString());
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
                        Console.WriteLine(e.ToString());
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
        
        public static void sendIcon(Icon pIcon)
        {
            Bitmap pBitmap = pIcon.ToBitmap();
            MemoryStream ms = new MemoryStream();
            pBitmap.Save(ms, pBitmap.RawFormat);
            byte[] iconBytes = ms.GetBuffer();
            pBitmap.Dispose();
            ms.Close();

            int total = 0;
            int size = data.Length;
            int dataleft = size;
            int sent;

            byte[] datasize = new byte[4];
            datasize = BitConverter.GetBytes(size);
            sent = handler.Send(datasize);

            while (total < size)
            {
                sent = handler.Send(iconBytes, total, dataleft, SocketFlags.None);
                total += sent;
                dataleft -= sent;
            }
        }
    }
}
