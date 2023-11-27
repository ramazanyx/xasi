package su.xash.fwgslib;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import org.json.*;
import android.preference.*;

/*
 * This utility class is intended to hide some Android and Java design-flaws and
 * also just shortcuts
 */
public class FWGSLib
{
	private static final String TAG = "FWGSLib";
	static String externalFilesDir;
	public static boolean FBitSet( final int bits, final int mask )
	{
		return ((bits & mask) != 0);
	}
	
	public static boolean FExactBitSet( final int bits, final int mask )
	{
		return ((bits & mask) == mask );
	}
	
	public static float atof( String str, float fallback )
	{
		float ret;
		try
		{
			ret = Float.valueOf( str );
		}
		catch( Exception e )
		{
			ret = fallback;
		}
		
		return ret;
	}
	
	public static int atoi( String str, int fallback )
	{
		int ret;
		try
		{
			ret = Integer.valueOf( str );
		}
		catch( Exception e )
		{
			ret = fallback;
		}
		
		return ret;
	}
	
	public static ApplicationInfo getApplicationInfo( Context ctx, String pkgName, int flags ) throws PackageManager.NameNotFoundException
	{
		PackageManager pm = ctx.getPackageManager();
		if( pkgName == null ) pkgName = ctx.getPackageName();
		return pm.getApplicationInfo( pkgName, flags );
	}
	
	public static String getNativeLibDir( Context ctx )
	{
		try
		{
			ApplicationInfo ai = getApplicationInfo( ctx, null, 0 );
			return ai.nativeLibraryDir;
		}
		catch( Exception e )
		{
			return ctx.getFilesDir().getParentFile().getPath() + "/lib";
		}
	}
	
	public static boolean checkGameLibDir( String gamelibdir, String allowed )
	{
		try
		{
			Log.d( TAG, " gamelibdir = " + gamelibdir + " allowed = " + allowed );
			
			if( gamelibdir.contains( "/.." ))
				return false;
			
			File f = new File( gamelibdir );
		
			if( !f.isDirectory() )
			{
				Log.d( TAG, "Not a directory" );
				return false;
			}
		
			if( !f.exists() )
			{
				Log.d( TAG, "Does not exist" );
				return false;
			}
			
			// add trailing / for simple regexp
			if( gamelibdir.charAt(gamelibdir.length() - 1) != '/' )
				gamelibdir = gamelibdir + "/";
					
			final String regex = ".+\\/" + allowed.replace(".",  "\\.") + "(|(-\\d))\\/(.+|)";
			
			Log.d( TAG, regex );
		
			final boolean ret =  gamelibdir.matches( regex );
			
			Log.d( TAG, "ret = " + ret );
			
			return ret;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getDefaultXashPath()
	{
		File dir = Environment.getExternalStorageDirectory();
		if( dir != null && dir.exists() )
			return dir.getPath() + "/xash";
		return "/sdcard/xash";
	}
	static class GetExternalFilesDir extends Thread
	{
		Context ctx;
		GetExternalFilesDir( Context ctx1 )
		{
			ctx = ctx1;
		}
		@Override
		public void run()
		{
			try
			{
				File f = ctx.getExternalFilesDir(null);

				f.mkdirs();

		 		externalFilesDir = f.getAbsolutePath();
				Log.d(TAG, "getExternalFilesDir success");
			}
			catch( Exception e )
			{
				Log.e( TAG, e.toString(), e);
			}
		}
	}
	public static String getExternalFilesDir( Context ctx )
	{
		if( externalFilesDir != null )
			return externalFilesDir;
		try
		{
			Thread t = new GetExternalFilesDir(ctx);
			t.start();
			t.join(2000);
		}
		catch(Exception e)
		{
			Log.e( TAG, e.toString(), e);
			externalFilesDir = getDefaultXashPath();
		}
		if( externalFilesDir == null )
			externalFilesDir = getDefaultXashPath();
		return externalFilesDir;
	}
	
	public static boolean isLandscapeOrientation( Activity act )
	{
		DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (metrics.widthPixels > metrics.heightPixels);
	}
	
	public static String getStringExtraFromIntent( Intent intent, String extraString, String ifNotFound )
	{
		String ret = intent.getStringExtra( extraString );
		if( ret == null )
		{
			ret = ifNotFound;
		}
		
		return ret;
	}

	public static void changeButtonsStyle( ViewGroup parent )
	{
		if( sdk >= 21 )
			return;
		
		for( int i = parent.getChildCount() - 1; i >= 0; i-- ) 
		{
			try
			{
				final View child = parent.getChildAt(i);
				
				if( child == null )
					continue;
				
				if( child instanceof ViewGroup )
				{
					changeButtonsStyle((ViewGroup) child);
					// DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED
				} 
				else if( child instanceof Button )
				{
					final Button b = (Button)child;
					final Drawable bg = b.getBackground();
					if(bg!= null)bg.setAlpha( 96 );
					b.setTextColor( 0xFFFFFFFF );
					b.setTextSize( 15f );
					//b.setText(b.getText().toString().toUpperCase());
					b.setTypeface( b.getTypeface(),Typeface.BOLD );
				}
				else if( child instanceof EditText )
				{
					final EditText b = ( EditText )child;
					b.setBackgroundColor( 0xFF353535 );
					b.setTextColor( 0xFFFFFFFF );
					b.setTextSize( 15f );
				}
			}
			catch( Exception e )
			{
			}
        }
    }


	
	public static final int sdk = Integer.valueOf(Build.VERSION.SDK);
}
