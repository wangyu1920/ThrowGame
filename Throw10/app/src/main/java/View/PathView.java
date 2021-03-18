package View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import PathRelatedClass.PathManager;
import PathRelatedClass.PathWithMode;

public class PathView extends View {
    public PathManager pathManager=new PathManager();
    //    画布大小参数
    private int height=0;
    private int width=0;
    public SharedPreferences preferences;
    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if (height == 0) {
            height=getHeight();
            width=getWidth();
        }
        if (pathManager.getOne("bound") == null) {
            PathWithMode pathWithMode=new PathWithMode("bound",
                    new Point(width/2,height+100),
                    2000,false,preferences);
            pathWithMode.moveTo(-5,height);
            pathWithMode.lineTo(width+5,height);
            pathWithMode.lineTo(width+5,height+15);
            pathWithMode.lineTo(-5,height+15);
            pathWithMode.lineTo(-5,height);
            pathWithMode.close();
            pathManager.addPath(pathWithMode);
        }
        pathManager.draw(canvas);
        super.onDraw(canvas);
    }
}
