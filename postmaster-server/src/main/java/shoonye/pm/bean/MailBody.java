package shoonye.pm.bean;

public class MailBody {
	private String plainText;
	private String html;
	
	public String getPlainText() {
		return plainText;
	}
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
    @Override
    public String toString() {
        return "MailBody [plainText=" + plainText + ", html=" + html + "]";
    }
	
}
