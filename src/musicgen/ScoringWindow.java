package musicgen;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import org.jfugue.Pattern;
import org.jfugue.Player;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A window that is used for playing music and ranking the songs that
 * are generated in order to improve the algorithm that generates the
 * music.
 *
 * @author Ches Burks
 *
 */
public class ScoringWindow extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = -246111507865348075L;

	private final JPanel contentPanel = new JPanel();

	private MusicGen generator;
	private Pattern currentPattern;
	private Genome currentGenome;
	private Player player;
	/**
	 * Launch the application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			ScoringWindow dialog = new ScoringWindow();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	Runnable play = new Runnable() {
		public void run() {
			if (!player.isPlaying()){
				player.play(currentPattern);
			}
		}
	};
	Runnable stop = new Runnable() {
		public void run() {
			if (player.isPlaying()){
				player.stop();
			}
		}
	};
	Runnable newGenome = new Runnable() {

		public void run() {
			if (player != null){
				if (player.isPlaying()){
					player.stop();
				}
			}
			currentGenome = new Genome();
			currentPattern = generator.getSong(currentGenome);
			player = new Player();
			textField.setText(currentGenome.toString());
		}
	};
	Runnable newSong = new Runnable() {

		public void run() {
			if (player != null){
				if (player.isPlaying()){
					player.stop();
				}
			}
			currentPattern = generator.getSong(currentGenome);
			player = new Player();
		}
	};
	Runnable pause = new Runnable() {
		public void run() {
			if (player.isPlaying()){
				player.pause();
			}
		}
	};
	Runnable resume = new Runnable() {
		public void run() {
			if (player.isPaused()){
				player.resume();
			}
		}
	};


	private JTextField textField;

	/**
	 * Create the dialog.
	 */
	public ScoringWindow() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // Nimbus is not available, use the default
		}
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (player != null){
					if (player.isPlaying()){
						player.stop();
					}
				}
			}
		});
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				(new Thread(pause)).start();
			}
			@Override
			public void focusGained(FocusEvent e) {
				(new Thread(resume)).start();
			}
		});
		setTitle("Scoring");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSlider slider = new JSlider();
			slider.setValue(3);
			slider.setToolTipText("Ranking of song");
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);
			slider.setPaintTicks(true);
			slider.setMajorTickSpacing(1);
			slider.setMinimum(1);
			slider.setMaximum(5);
			contentPanel.add(slider, BorderLayout.SOUTH);
		}
		{
			JButton btnSubmit = new JButton("Submit");
			btnSubmit.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {

				}
			});
			contentPanel.add(btnSubmit, BorderLayout.EAST);
		}
		{
			JButton btnNew = new JButton("New Genome");
			btnNew.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					(new Thread(newGenome)).start();
				}
			});
			contentPanel.add(btnNew, BorderLayout.WEST);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JButton button = new JButton("Play");
				button.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						(new Thread(play)).start();
					}
				});
				panel.add(button, BorderLayout.EAST);
			}
			{
				JButton btnStop = new JButton("Stop");
				panel.add(btnStop, BorderLayout.WEST);
				{
					textField = new JTextField();
					textField.setEditable(false);
					panel.add(textField, BorderLayout.CENTER);
					textField.setColumns(10);
				}
				{
					JButton btnGenerateNewSong = new JButton("Generate new song");
					btnGenerateNewSong.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							(new Thread(newSong)).start();
						}
					});
					panel.add(btnGenerateNewSong, BorderLayout.NORTH);
				}
				btnStop.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						(new Thread(stop)).start();
					}
				});
			}
		}
		generator = new MusicGen();
		(new Thread(newGenome)).start();
	}

}
