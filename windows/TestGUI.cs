using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Networking
{
    public partial class TestGUI : Form
    {
        delegate void updateGridDelegate(List<ProcessInterface> pList);
        delegate void updateTxtBoxDelegate(string cmd);
        delegate void updateGridDelegate2();

        public TestGUI()
        {
            InitializeComponent();
            //dataGridView1 = new DataGridView();
            dataGridView1.AutoGenerateColumns = true;
            //dataGridView1.ColumnCount = 2;
            dataGridView1.Columns.Add(new DataGridViewImageColumn());
            dataGridView1.Columns.Add(new DataGridViewTextBoxColumn());
            dataGridView1.Columns.Add(new DataGridViewTextBoxColumn());
            //dataGridView1.Columns.Add(new DataGridViewTextBoxColumn());
            dataGridView1.Columns[0].Name = "Icon";
            dataGridView1.Columns[1].Name = "Title";
            dataGridView1.Columns[2].Name = "ID";
            //dataGridView1.Columns[3].Name = "Responding";
            Communicator.sendCommand = new Communicator.commandRecieved(updateTextbox);
        }

        public void updateGridView(List<ProcessInterface> pList)
        {
            if (this.dataGridView1.InvokeRequired)
            {
                updateGridDelegate d = new updateGridDelegate(updateGridView);
                this.Invoke(d, new object[] {pList});
            }
            else
            {
                //dataGridView1.DataSource = ProcessHandler.getProcessListNames();
                dataGridView1.Rows.Clear();
                foreach (ProcessInterface pInt in pList)
                {
                    dataGridView1.Rows.Add(new object[] { pInt.getIcon(), pInt.getProcessName() });
                }
            }            
        }

        public void updateGridView2()
        {
            if (this.dataGridView1.InvokeRequired)
            {
                updateGridDelegate2 d = new updateGridDelegate2(updateGridView2);
                this.Invoke(d);
            }
            else
            {
                //dataGridView1.DataSource = ProcessHandler.getProcessListNames();
                dataGridView1.Rows.Clear();
                foreach (int key in ProcessHandler.getProcessDict().Keys)
                {
                    dataGridView1.Rows.Add(new object[] { ProcessHandler.getProcessDict()[key].getIcon(), ProcessHandler.getProcessDict()[key].getTitle(),
                        ProcessHandler.getProcessDict()[key].getHandle()});
                }
            }

        }

        public void updateTextbox(string cmd)
        {
            if (textBox1.InvokeRequired)
            {
                updateTxtBoxDelegate d = new updateTxtBoxDelegate(updateTextbox);
                Invoke(d, new object[] {cmd});
            }
            else
            {
                textBox1.AppendText("\r\n" +cmd);
            }
        }


        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {

        }

        private void button1_Click(object sender, EventArgs e)
        {
            ProcessHandler.openProgram((int)dataGridView1.SelectedRows[0].Cells[2].Value);
            updateGridView2();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            ProcessHandler.killProcess((int)dataGridView1.SelectedRows[0].Cells[2].Value);
            updateGridView2();
        }
    }
}
