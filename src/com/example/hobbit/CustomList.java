//package com.example.hobbit;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//public class CustomList extends BaseAdapter{
//
//    private final Activity context;
//    private final String[] web;
//    private final Integer[] imageId;
//    
//    public class item {
//        private String mImageUrl;
//        private String mTitle;
//        private String mDescription;
//    }
//    
//    public class ItemAdapter extends ArrayAdapter<Item> {
//        
//        public ItemAdapter(Context c, List<Item> items) {
//            super(c, 0, items);
//        }
//    
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ItemView itemView = (ItemView)convertView;
//            if (null == itemView)
//                itemView = ItemView.inflate(parent);
//            itemView.setItem(getItem(position));
//            return itemView;
//        }
//    
//    }
//    
////    public CustomList(Activity context, String[] web, Integer[] imageId) {
////        super();
////        this.context = context;
////        this.web = web;
////        this.imageId = imageId;
////    }
////    
////    @Override
////    public int getCount() {
////        // TODO Auto-generated method stub
////        return 0;
////    }
////
////    @Override
////    public Object getItem(int position) {
////        // TODO Auto-generated method stub
////        return null;
////    }
////
////    @Override
////    public long getItemId(int position) {
////        // TODO Auto-generated method stub
////        return 0;
////    }
////
////    @Override
////    public View getView(int position, View convertView, ViewGroup parent) {
////        LayoutInflater inflater = context.getLayoutInflater();
////        View rowView= inflater.inflate(R.layout.list_single, null, true);
////        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
////        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
////        txtTitle.setText(web[position]);
////        imageView.setImageResource(imageId[position]);
////        return rowView;
////    }
//
//}
