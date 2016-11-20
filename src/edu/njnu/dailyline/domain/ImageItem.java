package edu.njnu.dailyline.domain;

import java.io.Serializable;

/**
 * ä¸?¸ªå›¾ç‰‡å¯¹è±¡
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
