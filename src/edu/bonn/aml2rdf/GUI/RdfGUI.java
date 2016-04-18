package edu.bonn.aml2rdf.GUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import edu.bonn.aml2rdf.integration.Integrate;
import edu.bonn.aml2rdf.rdfconvertor.RDFConvertor;
import edu.isi.karma.webserver.KarmaException;

public class RdfGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	protected JFileChooser fc;
	private JTextArea textArea;
	protected static RdfGUI frame;
	public static File[] files_;
	static private final String newline = "\n";

	/**
	 * Create the frame.
	 */

	public static void display() {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new RdfGUI();

					frame.setVisible(true);
					frame.pack();
					frame.setSize(700, 300);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RdfGUI() {
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 465, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblRdfIntegration = new JLabel("AML 2 RDF");
		lblRdfIntegration.setFont(new Font("Serif", Font.PLAIN, 24));

		JLabel lblSelectYourRdf = new JLabel("Select your RDF files");
		File theDirectory = new File(System.getProperty("user.dir") + "\\model\\");
		fc.setCurrentDirectory(theDirectory);

		JButton btnRdfFile_1 = new JButton("AML Files");

		btnRdfFile_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				File theDirectory1 = new File(System.getProperty("user.dir") + "\\heterogeneity-examples\\");
				fc.setCurrentDirectory(theDirectory1);

				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					files_ = fc.getSelectedFiles();
					if (files_.length < 2) {
						JOptionPane.showMessageDialog(frame, "Please Select atleast two AML files.");
						btnRdfFile_1.doClick();
					} else {
						// This is where a real application would open the file.
						int i = 0;
						while (i < files_.length) {
							textArea.append("Loaded: " + files_[i].getName() + "." + newline);
							i++;
						}
					}
				} else {
					textArea.append("Open command cancelled by user." + newline);
				}
			}
		});

		JButton btnGenerateIntegration = new JButton("Integrate");
		btnGenerateIntegration.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				RDFConvertor rdf_conv = new RDFConvertor();
				try {
					rdf_conv.convertor();
					new Integrate().integrateRDF();
					textArea.append("Extracted: " + new RDFConvertor().getpath() + "integration.aml.ttl" + newline);

					try {
						Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL "
								+ new RDFConvertor().getpath() + "integration.aml");
					} catch (Exception e1) {
					}

				} catch (URISyntaxException | KarmaException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		JButton btnExit = new JButton("Exit");

		textArea = new JTextArea();
		textArea.setEditable(false);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(lblSelectYourRdf, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE).addGap(0))
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnGenerateIntegration, 0, 0, Short.MAX_VALUE).addComponent(btnRdfFile_1,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
								.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
						.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(lblRdfIntegration)
						.addContainerGap(191, Short.MAX_VALUE)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
						.addComponent(lblRdfIntegration, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE).addGap(17)
						.addComponent(lblSelectYourRdf).addGap(18)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup().addComponent(btnRdfFile_1).addGap(27)
										.addComponent(btnGenerateIntegration)))
						.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnExit).addGap(20)));
		contentPane.setLayout(gl_contentPane);
	}

	public static void main(String[] args) {
		RdfGUI.display();
	}

}
