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
            WindowMonitor wMonitor = new WindowMonitor(tGUI.updateGridView, tGUI.updateTextbox);
            Application.Run(tGUI);
        }
    }
}
