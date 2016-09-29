import java.awt.Point;

public class Letter {
	private final String LETTER;
	private boolean used;
	private Point point;

	Letter(String alphabet){
		this.LETTER = alphabet;
		this.used = false;
		this.point = new Point();
	}
	
	public void setUsed(boolean used){
		this.used = used;
	}

	public boolean getUsed(){
		return used;
	}
	public String getLetter(){
		return LETTER;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
}
