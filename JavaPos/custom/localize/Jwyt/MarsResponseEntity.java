package custom.localize.Jwyt;

import java.io.Serializable;

public class MarsResponseEntity implements Serializable
{
	private static final long serialVersionUID = -4014175358085035256L;
	private boolean success;
	private int httpStatus;
	private String content;
	private String text;

	public MarsResponseEntity()
	{
		super();
	}

	public MarsResponseEntity(boolean success, int httpStatus, String content)
	{
		super();
		this.success = success;
		this.httpStatus = httpStatus;
		this.content = content;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public int getHttpStatus()
	{
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus)
	{
		this.httpStatus = httpStatus;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return this.text;
	}
}
