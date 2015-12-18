package res.locale;

import java.util.Locale;
import java.util.ResourceBundle;

public class LangMan {
	private Locale locale;
	private ResourceBundle resBundle;
	
	
	public LangMan(Locale locale) {
		super();
		this.locale = locale;
		resBundle = ResourceBundle.getBundle("res/locale/value", locale);
	}

	public String getString(String key) {
		return resBundle.getString(key);
	}
}
