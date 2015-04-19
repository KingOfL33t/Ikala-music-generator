package midi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line from the mid2asc.exe output file.
 *
 * @author Ches Burks
 *
 */
public class Line {

	/**
	 * Checks to make sure the line is a valid MIDI ASCII file line.
	 *
	 * @param toCheck the line to check
	 * @return true if it matches a valid line
	 */
	public static boolean isValidLine(String toCheck) {
		return toCheck.matches(Line.regexLine);
	}

	private int bar;
	private float crotchet;
	private int track;
	private int channel;
	private String instruction;

	private String inputData;// null if it has been processed.

	// TODO javadoc
	final static String regexBar = "(BA +\\d+)";
	final static Pattern barPattern = Pattern.compile(Line.regexBar);
	final static String regexCrotchet = "(CR +\\d+(\\+\\d+)?(/\\d+)?)";
	final static Pattern crotchetPattern = Pattern.compile(Line.regexCrotchet);
	final static String regexTrack = "(TR +\\d+)";
	final static Pattern trackPattern = Pattern.compile(Line.regexTrack);
	final static String regexChannel = "(CH +\\d+)";
	final static Pattern channelPattern = Pattern.compile(Line.regexChannel);
	final static String regexInstrNote =
			"(NT +[A-G](#|b)?(\\'+|\\-+)? +\\d+(\\+\\d+)?(/\\d+)?( +von=\\d+)?( +voff=\\d+)?)";
	final static Pattern instrNotePattern = Pattern
			.compile(Line.regexInstrNote);
	final static String regexInstrStartNote = "(ST( +&[0-9A-F]{2}){3})";
	final static Pattern instrStartNotePattern = Pattern
			.compile(Line.regexInstrStartNote);
	// the quotes are basically any string thats one line
	final static String regexInstrString =
			"(Text type [1-7]: \\\".*\\\")";
	final static Pattern instrStringPattern = Pattern
			.compile(Line.regexInstrString);
	final static String regexInstrMeta =
			"(Meta Event +type &[0-9A-F]{2} *( \\d+)+)";
	final static Pattern instrMetaPattern = Pattern
			.compile(Line.regexInstrMeta);
	final static String regexInstrSysex =
			"(Sysex event &[0-9A-F]{2}( +&[0-9A-F]{2})+)";
	final static Pattern instrSysexPattern = Pattern
			.compile(Line.regexInstrSysex);
	final static String regexInstrEnd = "(End of track)";
	final static Pattern instrEndPattern = Pattern.compile(Line.regexInstrEnd);
	final static String regexInstrKey = "(Key [A-F](#|b)?( (major|minor))?)";
	final static Pattern instrKeyPattern = Pattern.compile(Line.regexInstrKey);
	final static String regexInstrTempo = "(Tempo \\d+(\\.\\d+)?)";
	final static Pattern instrTempoPattern = Pattern
			.compile(Line.regexInstrEnd);
	final static String regexInstrInstrument = "(Instrument \\d{1,2})";
	final static Pattern instrInstrumentPattern = Pattern
			.compile(Line.regexInstrInstrument);
	final static String regexInstrVolume = "(Channel volume \\d+)";
	final static Pattern instrVolumePattern = Pattern
			.compile(Line.regexInstrVolume);
	final static String regexInstrTime =
			"(Time signature \\d+/\\d+, clocks/mtick \\d+, crotchets/32ndnote \\d+)";
	final static Pattern instrTimePattern = Pattern
			.compile(Line.regexInstrTime);
	final static String regexInstruction = "(" + Line.regexInstrNote + "|"
			+ Line.regexInstrStartNote + "|" + Line.regexInstrString + "|"
			+ Line.regexInstrMeta + "|" + Line.regexInstrSysex + "|"
			+ Line.regexInstrEnd + "|" + Line.regexInstrKey + "|"
			+ Line.regexInstrTempo + "|" + Line.regexInstrInstrument + "|"
			+ Line.regexInstrVolume + "|" + Line.regexInstrTime + ")";
	final static Pattern instructionPattern = Pattern
			.compile(Line.regexInstruction);
	final static String regexLine = "\\A" + Line.regexBar + " +"
			+ Line.regexCrotchet + " +" + Line.regexTrack + " +"
			+ Line.regexChannel + " +" + Line.regexInstruction + "\\Z";
	final static Pattern linePattern = Pattern.compile(Line.regexLine);

	/**
	 * Creates a line with the given data. The line will have invalid data until
	 * parsed, which is not done in the constructor due to it being a relatively
	 * slow process.
	 *
	 * @param data the line to place into variables.
	 */
	public Line(String data) {
		this.bar = -1;
		this.crotchet = -1;
		this.track = -1;
		this.channel = -1;
		this.instruction = "";
		this.inputData = data;
	}

	/**
	 * Returns the bar this line has recorded.
	 * 
	 * @return the bar stored in this line
	 */
	public int getBar() {
		return this.bar;
	}

	/**
	 * Returns the channel this bar has recorded.
	 * 
	 * @return the channel stored in this line
	 */
	public int getChannel() {
		return this.channel;
	}

	/**
	 * Returns the crotchet this line has recorded.
	 * 
	 * @return the crotchet stored in this line
	 */
	public float getCrotchet() {
		return this.crotchet;
	}

	/**
	 * Returns the instruction this line has recorded.
	 * 
	 * @return the instruction stored in this line
	 */
	public String getInstruction() {
		return this.instruction;
	}

	private float getNumberFromFrac(String frac) {
		String[] parts = frac.split("\\+");
		if (parts.length < 1 || parts.length > 2) {
			return 0;
		}
		int top;
		int bottom;
		float ret;

		String[] fr2;

		if (parts.length == 1) {
			fr2 = parts[0].split("/");
		}
		else {
			fr2 = parts[1].split("/");
		}

		if (fr2.length == 1) {
			top = Integer.parseInt(fr2[0]);
			bottom = 1;
		}
		else if (fr2.length == 2) {
			top = Integer.parseInt(fr2[0]);
			bottom = Integer.parseInt(fr2[1]);
			if (parts.length == 2) {
				top += Integer.parseInt(parts[0]) * bottom;
			}
		}
		else {
			return 0;
		}
		ret = ((float) top) / ((float) bottom);

		return ret;

	}

	/**
	 * Returns the track this line has recorded.
	 * 
	 * @return the track stored in this line
	 */
	public int getTrack() {
		return this.track;
	}

	/**
	 * Returns true if the data has been parsed. Unparsed lines will have
	 * invalid data (bars, instructions, lines etc).
	 *
	 * @return true if the data has been parsed, false otherwise.
	 */
	public boolean isProcessed() {
		return this.inputData == null;
	}

	/**
	 * Outputs the information stored in this line to System.out
	 */
	public void printData() {
		System.out.print("bar: " + this.bar + " ");
		System.out.print("crotchet: " + this.crotchet + " ");
		System.out.print("track: " + this.track + " ");
		System.out.print("channel: " + this.channel + " ");
		System.out.print("instruction: " + this.instruction + " ");
		System.out.println();
	}

	/**
	 * Parses the line and sets up valid bar, crocket, track, channel, and input
	 * data. Will not parse twice.
	 */
	public void process() {
		if (this.inputData == null) {
			return;
		}
		if (!Line.isValidLine(this.inputData)) {
			System.out.println(this.inputData);
			this.inputData = null;
			return;
		}
		String sBar = null;
		String sCrotchet = null;
		String sTrack = null;
		String sChannel = null;
		String sInstruction = null;

		// Grab the strings from the data using regex's (gross, I know).
		// In braces to reduce variable scope
		{
			Matcher barMatcher = Line.barPattern.matcher(this.inputData);
			if (barMatcher.find()) {
				sBar =
						this.inputData.substring(barMatcher.start(),
								barMatcher.end());
			}
			else {
				return;
			}
			barMatcher.reset();
		}
		{
			Matcher crMatcher = Line.crotchetPattern.matcher(this.inputData);
			if (crMatcher.find()) {
				sCrotchet =
						this.inputData.substring(crMatcher.start(),
								crMatcher.end());
			}
			else {
				return;
			}
			crMatcher.reset();
		}
		{
			Matcher trackMatcher = Line.trackPattern.matcher(this.inputData);
			if (trackMatcher.find()) {
				sTrack =
						this.inputData.substring(trackMatcher.start(),
								trackMatcher.end());
			}
			else {
				return;
			}
			trackMatcher.reset();
		}
		{
			Matcher chanMatcher = Line.channelPattern.matcher(this.inputData);
			if (chanMatcher.find()) {
				sChannel =
						this.inputData.substring(chanMatcher.start(),
								chanMatcher.end());
			}
			else {
				return;
			}
			chanMatcher.reset();
		}
		{
			Matcher instMatcher =
					Line.instructionPattern.matcher(this.inputData);
			if (instMatcher.find()) {
				sInstruction =
						this.inputData.substring(instMatcher.start(),
								instMatcher.end());
			}
			else {
				return;
			}
			instMatcher.reset();
		}

		// Parse the smaller strings into data
		sBar = sBar.replaceAll("BA +", "");// make sBar just a number
		this.bar = Integer.parseInt(sBar);
		// turn sCrotchet into a number or fraction
		sCrotchet = sCrotchet.replaceAll("CR +", "");
		this.crotchet = this.getNumberFromFrac(sCrotchet);
		sTrack = sTrack.replaceAll("TR +", "");
		this.track = Integer.parseInt(sTrack);
		sChannel = sChannel.replaceAll("CH +", "");
		this.channel = Integer.parseInt(sChannel);

		this.instruction = sInstruction;// leave the instr for parsing later
		this.inputData = null;
	}
}
