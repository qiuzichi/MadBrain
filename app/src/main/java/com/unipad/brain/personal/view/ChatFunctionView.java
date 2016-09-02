package com.unipad.brain.personal.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.unipad.AppContext;
import com.unipad.brain.App;
import com.unipad.brain.R;
import com.unipad.brain.home.util.ConstSettings;
import com.unipad.brain.home.util.MyTools;
import com.unipad.brain.home.util.SharedPreferencesUtil;
import com.unipad.utils.FileUtil;
import com.unipad.utils.PicUtil;
import com.unipad.utils.ToastUtil;

/**
 * 描述 ： 聊天下的功能选项
 * 
 * @author gjb
 * 
 */
public class ChatFunctionView extends RelativeLayout implements OnClickListener {
	// 本类视图
	private View view;

	// 上下文对象
	private Context context;

	@ViewInject(R.id.img_zx)
	private ImageView mImgCamera;

	@ViewInject(R.id.img_xc)
	private ImageView mImgPic;

	public static final int Camera_flag = 1;

	public static final int Picture_flag = 2;

	private SharedPreferencesUtil preferencesUtil;

	private String path;

	private String photoPath;

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public ChatFunctionView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	public ChatFunctionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ChatFunctionView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public interface ChangeFileBack {

		void onSuccess(File file, Object object);

	}

	 public void setFileName(String path) {
	   this.path = path;
	 }

	public String getFileName() {
//		MyTools.showToast(context, preferencesUtil.getString("pathNames", ""));
		return preferencesUtil.getString("pathNames", "");
	};
	
	
	public void setFileName(){
		 preferencesUtil.saveString("pathNames", "");
	}

	/*
	 * 初始化
	 */
	private void init() {
		view = View.inflate(context, R.layout.chat_function_layout, this);
		preferencesUtil = new SharedPreferencesUtil(context);
		ViewUtils.inject(this, view);

		mImgCamera.setOnClickListener(this);

		mImgPic.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_zx:
			startCamera();
			break;
		case R.id.img_xc:
			startForPicFile();
			break;
		default:
			break;
		}
	}

	/**
	 * 开启系统相机服务
	 */
	private void startCamera() {

		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			MyTools.showToast(context, context.getResources().getString(R.string.util_camera_not_enable));
			return;
		}

		File mFile = getMainPhotoFile();
		if (null == mFile) {
			MyTools.showToast(context, context.getResources().getString(R.string.util_sdcard_not_enable));
			return;
		}

		if (context instanceof Activity) {

			path = ".jpg";

			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);

			File file = new File(App.getContext().getTakePhotoFile(), this.photoPath + path);
			if (file.exists()) {
				file.delete();
			}

			preferencesUtil.saveString("pathNames", App.getContext().getTakePhotoFile().getAbsolutePath() + "/"+ this.photoPath + path);
//			mPhotoFileList.put(v.getId(), file.getPath());

			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));// 设置系统相机拍摄照片完成后图片文件的存放地址
			((Activity) context).startActivityForResult(intent, Camera_flag);
//            MyTools.showToast(context, this.path);

//			Uri uri = Uri.fromFile(mSaveFile);
//
//			Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
//
//			getImageByCamera.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//
//			getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//			((Activity) context).startActivityForResult(getImageByCamera,
//					Camera_flag);
			return;
		}
		MyTools.showToast(context, "context in not the support type ");
	}

	/**
	 * 开启从图库获取图片路径
	 */
	private void startForPicFile() {

		if (context instanceof Activity) {
			Intent _intent = new Intent();
			_intent.setType("image/*");
			_intent.setAction(Intent.ACTION_GET_CONTENT);
			((Activity) context).startActivityForResult(_intent, Picture_flag);
			return;
		}
		MyTools.showToast(context, "context in not the support type ");
	}

	/**
	 * 拍照返回的结果bitmap
	 * 
	 * @param intent
	 * @return
	 */
	// public Bitmap parseCameraResult(Intent intent) {
	//
	// if (null == intent)
	// return null;
	// Bundle extras = intent.getExtras();
	// Bitmap myBitmap = (Bitmap) extras.get("data");
	// return myBitmap;
	//
	// }

	/**
	 * 获取从图库选择的图片路径
	 * 
	 * @param intent
	 *            Intent intent
	 * @param activity
	 *            Activity activity
	 * @return the image file
	 */
	public String parseImageFile(Intent intent, Activity activity) {
		if (null == intent)
			return null;

		Uri uri = intent.getData();
		String picPath = null;
		Cursor cursor=null;
		try {
			String[] pojo = { MediaStore.Images.Media.DATA };
			cursor = activity.managedQuery(uri, pojo, null, null, null);
			if (cursor != null) {
				ContentResolver cr = activity.getContentResolver();
				int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String path = cursor.getString(colunm_index);
				/***
				 * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
				 * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
				 */
				if (path.endsWith("jpg") || path.endsWith("png")) {
					picPath = path;
				} else {
					alert();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		
		}
		return picPath;
	}

	/**
	 * 显示dialog
	 */
	private void alert() {
		Dialog dialog = new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.util_hint))
				.setMessage(context.getResources().getString(R.string.util_pic_notuse_hint)).setPositiveButton(context.getResources().getString(R.string.util_affirm), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				})
				.create();
		dialog.show();
	}

	/**
	 * 获取拍照的图片文件路径
	 * 
	 * @return File file
	 */
	public File getMainPhotoFile() {
		File file = null;

		File childFile = null;
		try {
			if (FileUtil.hasSDCard()) {
				file = new File(FileUtil.getPath());
				if (!file.exists())
					file.mkdirs();
				childFile = new File(file, App.getContext().getTakePhotoFile().getPath());
				if (!childFile.exists())
					childFile.mkdirs();
			} else {
				ToastUtil.showToast(context.getResources().getString(R.string.util_sdcard_not_enable));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childFile;
	}

	/**
	 * 通过fileName来获取bitmap
	 * 
	 * @param fileName
	 *            String fileName
	 * @param context
	 *            Context context
	 * @return Bitmap bitmap
	 */
	public Bitmap getBitmapByFileName(String fileName, Context context) {
		File f = new File(fileName);
		try {
			Uri u = Uri.parse(MediaStore.Images.Media.insertImage(
					context.getContentResolver(), f.getAbsolutePath(), null,
					null));
			return PicUtil.compressImage(MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), u));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	/**
	 * 通过filename 来获取压缩后的Bitmap file
	 * @param fileName、
	 *  文件路径
	 * @param context
	 *   ApplicationContext
	 * @param back
	 * @see ChangeFileBack
	 */
	public void getFileByFileName(String fileName, Context context,ChangeFileBack back,Object object) {

		File f = new File(fileName);
		try {
			Uri u = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), f.getAbsolutePath(), null,null));

			Bitmap2File(back,MediaStore.Images.Media.getBitmap(context.getContentResolver(), u), fileName,object);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 获取图片和视频缩略图的
	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}


	/**
	 * bitmap 转为file
	 * 
	 * @param bmp
	 * @param fileName
	 * @return
	 */
	public void Bitmap2File(final ChangeFileBack back,final Bitmap bmp, final String fileName,final Object object) {
		if (bmp == null){
			back.onSuccess(null,object);
			return;
		}
		new Thread() {
			@Override
			public void run() {
				super.run();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmp.compress(CompressFormat.JPEG, 100, baos);
				int options = 100;
				while (baos.toByteArray().length / 1024 > 250) {
					baos.reset();
					bmp.compress(CompressFormat.JPEG, options, baos);
					options -= 10;
				}
				OutputStream stream = null;
				File file = null;
				try {
					file = new File(fileName);
					if (!file.exists())
						file.createNewFile();
					stream = new FileOutputStream(file);
					bmp.compress(CompressFormat.JPEG, options, stream);
					back.onSuccess(file,object);
				} catch (Exception e) {
					e.printStackTrace();
					back.onSuccess(null,object);
				}
			}
		}.start();
	}
}
