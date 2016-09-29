package com.jhf.photo.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
* @Title: ImageUtil.java 
* @Package com.android.support.framework.utils 
* @ClassName: ImageUtil 
* @Description: 图片工具类（内存中操作Bitmap）
* @author jiahongfei jiahongfeinew@163.com
* @date Nov 5, 2014 2:19:34 PM 
* @version V1.0.0
 */
public class ImageUtil {

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * bitmap转换成drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * drawable转换成bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	/**
	 * 从指定文件夹下获取图片
	 * 
	 * @param picPathString
	 * @return
	 */
	public static Bitmap getFolderPic(String picPathString) {
		return BitmapFactory.decodeFile(picPathString);
	}

	public static Bitmap convertViewToBitmap(View view) {
		// view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
		// MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	/**
	 * 在图片上加文字
	 * 
	 * @param photo
	 *            图片Bitmap
	 * @param string
	 *            添加的文字
	 * @param textSize
	 *            文字大小
	 * @param textColor
	 *            文字颜色
	 * @return
	 */
	public static Drawable watermark(Bitmap photo, String string,
			float textSize, int textColor) {

		int width = photo.getWidth(), hight = photo.getHeight();
		// System.out.println("宽" + width + "高" + hight);
		Bitmap icon = Bitmap
				.createBitmap(width, hight, Config.ARGB_8888); // 建立一个空的BItMap
		Canvas canvas = new Canvas(icon);// 初始化画布 绘制的图像到icon上

		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些

		Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
		Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
		canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
														// dst使用的填充区photoPaint

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setTextSize(textSize);// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(textColor);// 采用的颜色
		textPaint.setFakeBoldText(true);
		// textPaint.setShadowLayer(3f, 1,
		// 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
		FontMetrics sF = textPaint.getFontMetrics();
		int fontHeight = (int) Math.ceil(sF.descent - sF.top) + 2;
		Rect rect = new Rect();
		textPaint.getTextBounds(string, 0, string.length(), rect);
		canvas.drawText(string, photo.getWidth() / 2 - rect.width() / 2 - 2,
				rect.height() + 5, textPaint);// 绘制上去 字，开始未知x,y采用那只笔绘制
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// image.setImageBitmap(icon);

		return ImageUtil.bitmap2Drawable(icon);
	}

	/**
	 * 
	 * @param image
	 * @param size
	 *            多少kb
	 * @return
	 */
	public static byte[] compressImage(Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			System.out.println(baos.toByteArray().length / 1024);
			baos.reset();// 重置baos即清空baos
			image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		System.out.println(baos.toByteArray().length / 1024);
		return baos.toByteArray();
	}

	/**
	 * 返回Image宽高
	 * 
	 * @param file
	 * @return 返回数组，0元素width，1元素height
	 */
	public static int[] getImageBounds(File file) {
		int outWidth = 0;
		int outHeight = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		outWidth = options.outWidth;
		outHeight = options.outHeight;
		return new int[] { outWidth, outHeight };
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 
	 * @param fileName
	 * @return 功能：读取图片文件
	 */
	public static Bitmap readBitmap(String fileName) {

		File file = new File(fileName);

		if (file.exists()) {
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
				return bitmap;
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}

		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param bitmap
	 * @param fileName
	 *            功能：创建图片文件
	 */
	public static void saveBitmap(Bitmap bitmap, String fileName,
			CompressFormat format) {
		File file = new File(fileName);

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		FileOutputStream out;

		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(format, 80, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置图片，根据照片的角度进行旋转
	 * @param picPath 一定要是sd卡的路径
	 * @param bitmap
	 * @param imageView
	 */
	public static void setRotaingImageBitmap(String picPath, Bitmap bitmap, ImageView imageView){
		int degree = ImageUtil.readPictureDegree(picPath);
		Bitmap tempBitmap = null;
		if (Math.abs(degree) > 0) {
			tempBitmap = ImageUtil.rotaingImageView(degree, bitmap);
			
		}else {
			tempBitmap = bitmap;
		}
		imageView
				.setImageBitmap(tempBitmap);
	}
	
	/**
	 * 根据图片路径判断是否是图片
	 * @param imgPath
	 * @return true是图片，false不是图片
	 */
	public static boolean isBitmap(String imgPath){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		Bitmap tempBitmap = BitmapFactory.decodeFile(imgPath, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		System.out.println("outWidth : " + outWidth);
		System.out.println("outHeight : " + outHeight);
		System.out.println("----------------------------------------");
		if (outHeight >= outWidth) {
			options.inSampleSize = outHeight;
		} else {
			options.inSampleSize = outWidth;
		}
		options.inJustDecodeBounds = false;
		tempBitmap =  BitmapFactory.decodeFile(imgPath, options);
		if(null == tempBitmap){
			return false;
		}
		System.out.println("tempBitmap : " + tempBitmap.getRowBytes()*tempBitmap.getHeight());
		System.out.println("getWidth : " + tempBitmap.getWidth());
		System.out.println("getHeight : " + tempBitmap.getHeight());
		tempBitmap.recycle();
		tempBitmap = null;
		return true;
	}
	
//	/**
//	 * getDrawbleBitmapColor:获取图片的颜色. <br/>
//	 *
//	 * @author denghcuan
//	 * @param context
//	 * @param drawableId  图片的drawable地址
//	 * @param defaultColor  默认颜色，无法获取图片颜色，将返回该色值
//	 * @return
//	 * @since JDK 1.6
//	 */
//	public static Integer getDrawbleBitmapColor(Context context, int drawableId,int defaultColor) {
//		if(context == null || drawableId < 0){
//			return null;
//		}
//		Resources resources = context.getResources();
//		Bitmap bm = BitmapFactory.decodeResource(resources, drawableId);
//		Palette palette = Palette.generate(bm);
//		bm.recycle();
//		return palette.getLightVibrantColor(resources.getColor(
//				defaultColor));
//	}
//	/**
//	 *
//	 * getDrawbleBitmapColor:获取图片的颜色. <br/>
//	 *
//	 * @author denghcuan
//	 * @param context
//	 * @param picPathString  图片的文件地址，需要保证传入的文件地址是可用的图片文件地址
//	 * @param defaultColor
//	 * @return
//	 * @since JDK 1.6
//	 */
//	public static Integer getDrawbleBitmapColor(Context context, String picPathString,int defaultColor) {
//		if(context == null || picPathString == null){
//			return null;
//		}
//		Resources resources = context.getResources();
//		Bitmap bm = getFolderPic(picPathString);
//		Palette palette = Palette.generate(bm);
//		bm.recycle();
//		return palette.getLightVibrantColor(resources.getColor(
//				defaultColor));
//	}
}
