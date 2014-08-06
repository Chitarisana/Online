package model.utils;

public class DialogContent {
	private String tag, message;
	private int posButton, negButton, type;
	private boolean isShowing;

	public DialogContent(String tag, String message, int posButton,
			int negButton, int type) {
		this.setTag(tag);
		this.setMessage(message);
		this.setPosButton(posButton);
		this.setNegButton(negButton);
		this.setType(type);
		this.setShowing(false);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPosButton() {
		return posButton;
	}

	public void setPosButton(int posButton) {
		this.posButton = posButton;
	}

	public int getNegButton() {
		return negButton;
	}

	public void setNegButton(int negButton) {
		this.negButton = negButton;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isShowing() {
		return isShowing;
	}

	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}
}