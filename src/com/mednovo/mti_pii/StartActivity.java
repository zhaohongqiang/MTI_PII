package com.mednovo.mti_pii;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class StartActivity extends Activit
	private ImageView welcomeImg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_main);
		welcomeImg = (ImageView) this.findViewById(R.id.welcome_img);
		AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);
		anima.setDuration(3000);// ���ö�����ʾʱ��
		welcomeImg.startAnimation(anima);
		anima.setAnimationListener(new AnimationImpl());

	}

	private class AnimationImpl implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
			//welcomeImg.setBackgroundResource(R.drawable.welcome);
			welcomeImg.setBackgroundResource(R.drawable.mst);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			skip(); // ������������ת�����ҳ��
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}

	private void skip() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}
