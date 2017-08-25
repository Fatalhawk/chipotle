using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace Networking
{
    interface IProgramInterface
    {
        void initProcessIcon();
        void performAction();
        string getWindowTitle();
        string getProcessName();
        Icon getIcon();
        int getHandle();

        void OnWindowTitleChange();




    }
}
