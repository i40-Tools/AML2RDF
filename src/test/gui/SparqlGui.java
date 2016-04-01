package test.gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import test.sparql.Sparql;

public class SparqlGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	protected JFileChooser fc;
//	private JTextArea log;
	private JTextArea textArea;
	public static File file2;
	public static File file;
	static private final String newline = "\n";
	/**
	 * Launch the application.
	 * @return 
	 */
	public static void go() {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SparqlGui frame = new SparqlGui();
					frame.setVisible(true);
					frame.setSize(500,300);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SparqlGui() {
		fc = new JFileChooser();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 350, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblRdfIntegration = new JLabel("               RDF integration");
		lblRdfIntegration.setFont(new Font("Serif", Font.PLAIN, 24));
		
		JLabel lblSelectYourRdf = new JLabel("Select your RDF files");
		
		JButton btnRdfFile = new JButton("Rdf  File-1");
		btnRdfFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
				 file = fc.getSelectedFile();
				
				//This is where a real application would open the file.
				textArea.append("Opening: " + file.getName()+ "." + newline);
				} else {
				textArea.append("Open command cancelled by user." + newline);
				}

			}
		});
		
		JButton btnRdfFile_1 = new JButton("Rdf File -2");
		btnRdfFile_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
				 file2 = fc.getSelectedFile();
				
				//This is where a real application would open the file.
				textArea.append("Opening: " + file2.getName()+ "." + newline);
				} else {
				textArea.append("Open command cancelled by user." + newline);
				}
			}
		});
		
		
		JButton btnGenerateIntegration = new JButton("Generate Integration");
		btnGenerateIntegration.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String l=Sparql.integrate_double();
				textArea.append(l.toString());
			}
		});
		JButton btnExit = new JButton("Exit");
		
		 textArea = new JTextArea();
	     textArea.setEditable(false);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblRdfIntegration, GroupLayout.PREFERRED_SIZE, 314, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblSelectYourRdf, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
					.addGap(0))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(btnGenerateIntegration, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(btnRdfFile_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnRdfFile, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(28)
							.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblRdfIntegration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(17)
					.addComponent(lblSelectYourRdf)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnRdfFile)
							.addGap(26)
							.addComponent(btnRdfFile_1))
						.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnGenerateIntegration)
						.addComponent(btnExit))
					.addGap(20))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
}
