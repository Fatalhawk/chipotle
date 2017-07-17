/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

//UPDATE TO DICTIONARIES

using System;
using System.Diagnostics;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Automation;

namespace Networking
{
    /**
     * Class to define base operations on process
     * Meant to be inherited by more app-specific classes
     **/ 
    public class ProcessInterface
    {
        private Process process;
        //process encapsulated in processInterface 
        public Process ProcessProp {
            get { return process; }
            set { process = value; }
        }

        //process's main handle as observed by program
        private IntPtr handle;
        //process's icon
        private Icon processIcon;

        //used for retrieving window handle
        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool SetForegroundWindow(IntPtr hWnd);

        /**
         * Constructor
         * @param: Process that this object manages
         **/
        public ProcessInterface(Process pObj)
        {
            process = pObj;
            initProcessHandle();
            initProcessIcon();
        }

        /**
         * obtains the main window handle of the process
         * used to be able to send windows system/app commands
         **/
        private void initProcessHandle()
        {
            // find window handle of Notepad
            handle = process.MainWindowHandle;//FindWindow(process.MainWindowHandle, "");
            if (!handle.Equals(IntPtr.Zero)){}
        }

        private void initProcessIcon()
        {
            try
            {
                processIcon = Icon.ExtractAssociatedIcon(process.MainModule.FileName);
            }
            //for each catch send an app-default icon to the phone to use as a placeholder
            catch (System.ComponentModel.Win32Exception e)
            {
                processIcon = null;
                //Console.WriteLine(e.ToString());
            }
            catch (ArgumentException e)
            {
                processIcon = null;
                //Console.WriteLine(e.ToString());
            }
            catch(Exception e)
            {
                processIcon = null;
                //Console.WriteLine(e.ToString());
            }
        }

        /**
         * makes window of process the foreground window
         **/
        public bool setForegroundApp()
        {
            if (!SetForegroundWindow(handle))
            {
                return false;
            }
            return true;
        }

        /**
         * terminates the process and closes the window of the process
         **/
        public bool killApp()
        {
            try
            {
                //setForegroundApp();
                process.CloseMainWindow();
                process.Close();
                if (!process.HasExited)
                {
                    AutomationElement element = AutomationElement.FromHandle(process.MainWindowHandle);
                    if (element != null)
                    {
                        ((WindowPattern)element.GetCurrentPattern(WindowPattern.Pattern)).Close();
                    }

                }
                return true;
            }
            catch (NullReferenceException e)
            {
                process.Kill();
            }
            catch (Exception e)
            {
                //Console.WriteLine("Error: {0}", e.ToString());
                return false;
                process.Kill();
            }
            return true;
        }
        
        /**
         * Accessor Methods 
         **/ 
        public void printProcessInfo()
        {
            Console.WriteLine("{0}\tID: {1}\tHandle: {1}", process.ProcessName, process.Id, process.MainWindowHandle);
        }

        public string getTitle()
        {
            return process.MainWindowTitle;
        }

        public Icon getIcon()
        {
            return processIcon;
        }

        public string getProcessName()
        {
            try
            {
                return process.ProcessName;
            }
            catch (InvalidOperationException e)
            {
                return "lol";
            }
        }

        public bool Equals(ProcessInterface otherPI)
        {
            return (process.Id == otherPI.process.Id);
        }
    }
}
