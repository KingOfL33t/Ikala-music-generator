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
import markov.SaveHandler;
import midi.Song;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

/**
 * Allows you to load and parse midi files to a format the program can use.
 *
 * @author Ches Burks
 *
 */
class MidiInterface {

	JFrame MainFrame;
	Chain logicChain = new markov.Chain();
	private int threads = 0;
	private int posts = 0;
	private int page = 0;

	private JLabel lblNumPosts;
	private JLabel lblNumThreads;
	JLabel lblNumChains;
	JLabel lblStatus;
	private JLabel lblPage;

	private JPanel panel_2;
	JTextPane postField;
	private JPanel panel_3;
	private JScrollPane scrollPane;

	private JScrollPane scrollPane_post;

	Player player = new Player();
	Pattern pat;
	private JButton btnLoadMidi;

	private class LoadThread extends Thread {
		@Override
		public void run() {
			MidiInterface.this.lblStatus.setText("Status: loading...");
			JFileChooser filepicker = new JFileChooser();
			filepicker.showSaveDialog(new JDialog());
			if (filepicker.getSelectedFile() == null) {
				MidiInterface.this.postField
						.setText("You did not pick a file.");
				return;
			}
			if (!filepicker.getSelectedFile().exists()) {
				MidiInterface.this.postField
						.setText("That file does not exist.");
				return;
			}
			if (!filepicker.getSelectedFile().canRead()) {
				MidiInterface.this.postField
						.setText("Do not have permission to read.");
				return;
			}

			try {
				MidiInterface.this.pat =
						MidiFileManager.loadPatternFromMidi(filepicker
								.getSelectedFile());
				MidiInterface.this.postField.setText(MidiInterface.this.pat
						.toString());
				MidiInterface.this.player.play(MidiInterface.this.pat);

			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			MidiInterface.this.lblStatus.setText("Status: idle");
			MidiInterface.this.lblNumChains.setText("# Chains: "
					+ MidiInterface.this.logicChain.getKnownWords().size());
		}
	}

	private class LoadMidiThread extends Thread {
		@Override
		public void run() {
			MidiInterface.this.lblStatus.setText("Status: loading...");
			JFileChooser filepicker = new JFileChooser();
			filepicker.showSaveDialog(new JDialog());
			if (filepicker.getSelectedFile() == null) {
				MidiInterface.this.postField
						.setText("You did not pick a file.");
				return;
			}
			if (!filepicker.getSelectedFile().exists()) {
				MidiInterface.this.postField
						.setText("That file does not exist.");
				return;
			}
			if (!filepicker.getSelectedFile().canRead()) {
				MidiInterface.this.postField
						.setText("Do not have permission to read.");
				return;
			}

			try {
				Song song = SaveHandler.loadSong(filepicker.getSelectedFile());

			}
			catch (Exception e) {
				MidiInterface.this.postField.setText("Invalid file");
				MidiInterface.this.logicChain.clear();
				MidiInterface.this.updateChains();
			}

			MidiInterface.this.lblStatus.setText("Status: idle");
			MidiInterface.this.lblNumChains.setText("# Chains: "
					+ MidiInterface.this.logicChain.getKnownWords().size());
		}
	}

	private class SaveThread extends Thread {
		@Override
		public void run() {
			MidiInterface.this.lblStatus.setText("Status: Saving...");
			JFileChooser filepicker = new JFileChooser();
			filepicker.showSaveDialog(new JDialog());
			if (filepicker.getSelectedFile() == null) {
				MidiInterface.this.postField
						.setText("You did not pick a file.");
				return;
			}
			if (filepicker.getSelectedFile().exists()
					&& !filepicker.getSelectedFile().canWrite()) {
				MidiInterface.this.postField.setText("Cannot overwrite file.");
				return;
			}
			// SaveHandler.saveChain(logicChain, filepicker.getSelectedFile());
			if (MidiInterface.this.pat != null) {
				try {
					MidiInterface.this.pat.save(filepicker.getSelectedFile());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			MidiInterface.this.lblStatus.setText("Status: idle");
		}
	}

	/**
	 * Returns the main programs markov chain.
	 *
	 * @return the logic chain this program handles
	 */
	public Chain getChain() {
		return this.logicChain;
	}

	/**
	 * Launch the application.
	 *
	 * @param args arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
		++this.threads;
		this.lblNumThreads.setText("Threads read: " + this.threads);
	}

	/**
	 * Increases and updates the post number label
	 */
	public void incrementPostsRead() {
		++this.posts;
		this.lblNumPosts.setText("Posts read: " + this.posts);
	}

	/**
	 * Increases and updates the page number
	 */
	public void incrementPageNumber() {
		++this.page;
		this.lblPage.setText("Page: " + this.page);
	}

	/**
	 * Updates the label for number of chains
	 */
	public void updateChains() {
		this.lblNumChains.setText("# Chains: "
				+ this.logicChain.getKnownWords().size());
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
			this.lblNumPosts.setText(text);
			break;
		case "threads":
			this.lblNumThreads.setText(text);
			break;
		case "chains":
			this.lblNumChains.setText(text);
			break;
		case "status":
			this.lblStatus.setText(text);
			break;
		default:
			break;
		}
	}

	/**
	 * Create the application.
	 */
	public MidiInterface() {
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.MainFrame = new JFrame();
		this.MainFrame.setTitle("Midi interface");
		this.MainFrame.setBounds(100, 100, 601, 433);
		this.MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.MainFrame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		this.MainFrame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 5, 0, 0));

		this.lblStatus = new JLabel("Status:");
		panel.add(this.lblStatus);

		this.lblNumChains = new JLabel("# Chains:");
		panel.add(this.lblNumChains);

		this.lblPage = new JLabel("Page: ");

		panel.add(this.lblPage);

		this.lblNumThreads = new JLabel("Threads read:");

		panel.add(this.lblNumThreads);

		this.lblNumPosts = new JLabel("Posts Read:");
		panel.add(this.lblNumPosts);

		JPanel panel_1 = new JPanel();
		this.MainFrame.getContentPane().add(panel_1, BorderLayout.WEST);
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

		this.btnLoadMidi = new JButton("Load from MIDI ASCII");
		this.btnLoadMidi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				(new LoadMidiThread()).start();
			}
		});
		panel_1.add(this.btnLoadMidi);
		panel_1.add(btnSaveData);

		this.panel_3 = new JPanel();
		this.MainFrame.getContentPane().add(this.panel_3, BorderLayout.EAST);
		this.panel_3.setLayout(new BorderLayout(0, 0));

		this.scrollPane = new JScrollPane();
		this.panel_3.add(this.scrollPane, BorderLayout.CENTER);

		this.panel_2 = new JPanel();
		this.MainFrame.getContentPane().add(this.panel_2, BorderLayout.CENTER);
		this.panel_2.setLayout(new BorderLayout(0, 0));
		this.scrollPane_post = new JScrollPane();
		this.panel_2.add(this.scrollPane_post, BorderLayout.CENTER);
		this.postField = new JTextPane();
		this.scrollPane_post.setViewportView(this.postField);
	}

	/**
	 * Returns a random string based on the gathered data.
	 *
	 * @param length how many notes the given string should be
	 * @return the newly created post
	 */
	public String generateSong(int length) {
		String ret = "";
		String tmp = this.getRandomNote();
		ret += tmp;

		for (int i = 0; i < length; ++i) {
			if (this.logicChain.hasWord(tmp)) {
				for (Link link : this.logicChain.getKnownWords()) {
					if (link.getWord().equals(tmp)) {
						tmp = this.getRandomChild(link);
						ret += " " + tmp;
					}
				}
			}
			else {
				tmp = this.getRandomNote();
				ret += " " + tmp;
			}
		}
		return ret;
	}

	/**
	 * Returns a weighted random string in the list. A child with higher
	 * occurrences return more often. This is not efficient.
	 *
	 * @param link the link to return children for
	 *
	 * @return a random child
	 */
	private String getRandomChild(Link link) {
		LinkedList<String> strings = new LinkedList<>();
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
					(int) (Math.random() * this.logicChain.getKnownWords()
							.size());
			return this.logicChain.getKnownWords().get(index).getWord();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return " ";
		}
	}

}
