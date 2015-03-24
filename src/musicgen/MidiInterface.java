
package musicgen;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import markov.Chain;
import markov.Child;
import markov.Link;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

/**
 * Allows you to load and parse midi files to a format the program can use.
 * 
 * @author Ches Burks
 *
 */
public class MidiInterface {

	private JFrame MainFrame;
	private Chain logicChain = new markov.Chain();
	private int threads = 0;
	private int posts = 0;
	private int page = 0;

	private JLabel lblNumPosts;
	private JLabel lblNumThreads;
	private JLabel lblNumChains;
	private JLabel lblStatus;
	private JLabel lblPage;

	private JPanel panel_2;
	private JTextPane postField;
	private JPanel panel_3;
	private JScrollPane scrollPane;

	private JScrollPane scrollPane_post;

	private Player player = new Player();
	private Pattern pat;

	private class LoadThread extends Thread {
		@Override
		public void run() {
			lblStatus.setText("Status: loading...");
			JFileChooser filepicker = new JFileChooser();
			filepicker.showSaveDialog(new JDialog());
			if (filepicker.getSelectedFile() == null) {
				postField.setText("You did not pick a file.");
				return;
			}
			if (!filepicker.getSelectedFile().exists()) {
				postField.setText("That file does not exist.");
				return;
			}
			if (!filepicker.getSelectedFile().canRead()) {
				postField.setText("Do not have permission to read.");
				return;
			}
			/*
			 * try { SaveHandler.loadToChain(logicChain,
			 * filepicker.getSelectedFile()); } catch (Exception e) {
			 * postField.setText("Invalid file"); logicChain.clear();
			 * updateChains(); }
			 */

			try {
				pat =
						MidiFileManager.loadPatternFromMidi(filepicker
								.getSelectedFile());
				postField.setText(pat.toString());
				player.play(pat);

			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			lblStatus.setText("Status: idle");
			// lblNumChains.setText("# Chains: "
			// + logicChain.getKnownWords().size());
		}
	}

	private class SaveThread extends Thread {
		@Override
		public void run() {
			lblStatus.setText("Status: Saving...");
			JFileChooser filepicker = new JFileChooser();
			filepicker.showSaveDialog(new JDialog());
			if (filepicker.getSelectedFile() == null) {
				postField.setText("You did not pick a file.");
				return;
			}
			if (filepicker.getSelectedFile().exists()
					&& !filepicker.getSelectedFile().canWrite()) {
				postField.setText("Cannot overwrite file.");
				return;
			}
			// SaveHandler.saveChain(logicChain, filepicker.getSelectedFile());
			if (pat != null) {
				try {
					pat.save(filepicker.getSelectedFile());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			lblStatus.setText("Status: idle");
		}
	}

	/**
	 * Returns the main programs markov chain.
	 * 
	 * @return the logic chain this program handles
	 */
	public Chain getChain() {
		return logicChain;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MidiInterface window = new MidiInterface();
					window.MainFrame.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Increases and updates the threads read label
	 */
	public void incrementThreadsRead() {
		++threads;
		lblNumThreads.setText("Threads read: " + threads);
	}

	/**
	 * Increases and updates the post number label
	 */
	public void incrementPostsRead() {
		++posts;
		lblNumPosts.setText("Posts read: " + posts);
	}

	/**
	 * Increases and updates the page number
	 */
	public void incrementPageNumber() {
		++page;
		lblPage.setText("Page: " + page);
	}

	/**
	 * Updates the label for number of chains
	 */
	public void updateChains() {
		lblNumChains.setText("# Chains: " + logicChain.getKnownWords().size());
	}

	/**
	 * Sets the supplied label to the given text. Valid labels are:
	 * <ul>
	 * <li>posts</li>
	 * <li>threads</li>
	 * <li>chains</li>
	 * <li>status</li>
	 * <ul>
	 * 
	 * @param label the label to set
	 * @param text the text
	 */
	public void setLabel(String label, String text) {
		switch (label) {
		case "posts":
			lblNumPosts.setText(text);
			break;
		case "threads":
			lblNumThreads.setText(text);
			break;
		case "chains":
			lblNumChains.setText(text);
			break;
		case "status":
			lblStatus.setText(text);
			break;
		default:
			break;
		}
	}

	/**
	 * Create the application.
	 */
	public MidiInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		MainFrame = new JFrame();
		MainFrame.setTitle("Midi interface");
		MainFrame.setBounds(100, 100, 601, 433);
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainFrame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		MainFrame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 5, 0, 0));

		lblStatus = new JLabel("Status:");
		panel.add(lblStatus);

		lblNumChains = new JLabel("# Chains:");
		panel.add(lblNumChains);

		lblPage = new JLabel("Page: ");

		panel.add(lblPage);

		lblNumThreads = new JLabel("Threads read:");

		panel.add(lblNumThreads);

		lblNumPosts = new JLabel("Posts Read:");
		panel.add(lblNumPosts);

		JPanel panel_1 = new JPanel();
		MainFrame.getContentPane().add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));

		JButton btnLoadData = new JButton("Load Data");
		btnLoadData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				(new LoadThread()).start();

			}
		});
		panel_1.add(btnLoadData);

		JButton btnSaveData = new JButton("Save Data");
		btnSaveData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				(new SaveThread()).start();
			}
		});
		panel_1.add(btnSaveData);

		panel_3 = new JPanel();
		MainFrame.getContentPane().add(panel_3, BorderLayout.EAST);
		panel_3.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel_3.add(scrollPane, BorderLayout.CENTER);

		panel_2 = new JPanel();
		MainFrame.getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		scrollPane_post = new JScrollPane();
		panel_2.add(scrollPane_post, BorderLayout.CENTER);
		postField = new JTextPane();
		scrollPane_post.setViewportView(postField);
	}

	/**
	 * Returns a random string based on the gathered data.
	 * 
	 * @param length how many notes the given string should be
	 * @return the newly created post
	 */
	public String generateSong(int length) {
		String ret = "";
		String tmp = getRandomNote();
		ret += tmp;

		for (int i = 0; i < length; ++i) {
			if (logicChain.hasWord(tmp)) {
				for (Link link : logicChain.getKnownWords()) {
					if (link.getWord().equals(tmp)) {
						tmp = getRandomChild(link);
						ret += " " + tmp;
					}
				}
			}
			else {
				tmp = getRandomNote();
				ret += " " + tmp;
			}
		}
		return ret;
	}

	/**
	 * Returns a weighted random string in the list. A child with higher
	 * occurrences return more often. This is not efficient.
	 * 
	 * @return a random child
	 */
	private String getRandomChild(Link link) {
		LinkedList<String> strings = new LinkedList<String>();
		int i;
		for (Child child : link.getList()) {
			for (i = 0; i < child.getOccuranceCount(); ++i) {
				strings.add(child.getWord());
			}
		}

		String str = strings.get((int) (Math.random() * strings.size()));

		strings.clear();
		return str;
	}

	/**
	 * Returns a random note or space if there was an error.
	 * 
	 * @return a note
	 */
	private String getRandomNote() {
		try {
			int index =
					(int) (Math.random() * logicChain.getKnownWords().size());
			return logicChain.getKnownWords().get(index).getWord();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return " ";
		}
	}

}
