package ucr.example.arce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import ucr.example.arce.R;

import java.util.List;

public class ImageAdapter extends PagerAdapter {

    List<Integer> list;
    ImageAdapter(List<Integer> imageList){
        this.list = imageList;
    }

    @Override
    public int getCount() {
        return 0;
    }

    /*
     * Do: Llamado objeto imagen
     * Param: ViewGroup container, Object object
     * Return: view
     * */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /*
     * Do: Se instancia la imagen respectiva
     * Param: ViewGroup container, int position
     * Return: view de la imagen
     * */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.image_layout, container, false);
        ImageView image = view.findViewById(R.id.imageView);
        image.setImageResource(list.get(position));
        container.addView(view);
        return view;
    }

    /*
     * Do: Se destruye la vista de la imagen
     * Param: ViewGroup container, int position
     * Return: none
     * */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
