using System;
using System.Collections.Generic;
using System.Windows.Forms;
using App.Program;

namespace Networking
{
    public partial class TestGUI : Form
    {
        delegate void updateGridDelegate(List<ProgramBase> pList);
        delegate void updateTxtBoxDelegate(string cmd);
        delegate void updateGridDelegate2();

        public TestGUI()
        {
            InitializeComponent();
            //dataGridView1 = new DataGridView();
            dataGridView1.AutoGenerateColumns = true;
            //dataGridView1.ColumnCount = 2;
            dataGridView1.RowTemplate.Height = 50;
            dataGridView1.Columns.Add(new DataGridViewImageColumn());
            dataGridView1.Columns.Add(new DataGridViewTextBoxColumn());
            dataGridView1.Columns.Add(new DataGridViewTextBoxColumn());
            dataGridView1.Columns.Add(new DataGridViewImageColumn());
            dataGridView1.Columns[0].Name = "Icon";
            dataGridView1.Columns[1].Name = "Title";
            dataGridView1.Columns[2].Name = "ID";
            dataGridView1.Columns[3].Name = "Screencap";
        }

        public void updateGridView(List<ProgramBase> pList)
        {
            if (this.dataGridView1.InvokeRequired)
            {
                updateGridDelegate d = new updateGridDelegate(updateGridView);
                this.Invoke(d, new object[] {pList});
            }
            else
            {
                //dataGridView1.DataSource = ProgramManager.getProcessListNames();
                dataGridView1.Rows.Clear();
                foreach (ProgramBase pInt in pList)
                {
                    dataGridView1.Rows.Add(new object[] { pInt.getIcon(), pInt.AssociatedProcess.ProcessName });
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
                //dataGridView1.DataSource = ProgramManager.getProcessListNames();
                dataGridView1.Rows.Clear();
                foreach (int key in ProgramManager.ProcessDict.Keys)
                {
                    dataGridView1.Rows.Add(new object[] { ProgramManager.ProcessDict[key].getIcon(), ProgramManager.ProcessDict[key].WindowTitle,
                        ProgramManager.ProcessDict[key].getHandle(), ProgramManager.ProcessDict[key].WindowCap});
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
            ProgramManager.focusProgram((int)dataGridView1.SelectedRows[0].Cells[2].Value);
            updateGridView2();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            ProgramManager.removeProgram((int)dataGridView1.SelectedRows[0].Cells[2].Value);
            updateGridView2();
        }

        private void sendButton_Click(object sender, EventArgs e)
        {
            //ProgramManager.getPInterface((int)dataGridView1.SelectedRows[0].Cells[2].Value).performAction(Int32.Parse(cmdTxtBox.Text));
        }

        private void cmdTxtBox_TextChanged(object sender, EventArgs e)
        {

        }

        private void TestGUI_Load(object sender, EventArgs e)
        {

        }
    }
}
