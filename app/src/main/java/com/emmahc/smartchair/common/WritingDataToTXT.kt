package com.emmahc.smartchair.common

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception

//mContext.getApplicationContext().getFilesDir()-data/user/0/com.emmahc.smartchair/files
//Environment.getExternalStorageDirectory().getAbsolutePath() - /storage/emulated/0

//데이터를 텍스트 파일에 쓰기 위한 클래스
class WritingDataToTXT() {
    fun writingText(contents:String){
        try{
            //C:\Users\kramf\Desktop\Android Projects\Smart Chair\reclyner\app\src\main\java\com\emmahc\smartchair\common
            val absoluteDir:String = "src/main/java/com/emmahc/smartchair/common"
            //파일 생성
            val file_name:String = "input_data.txt"
            val dir:File = File(absoluteDir)
            if(dir.exists()){
                Log.d("writing","path not found")
                dir.mkdir()
            }
            var fos: FileOutputStream? = null
            //경로가 없다면
            try{
                fos =  FileOutputStream("${dir}/${file_name}",true)
            }catch (e:Exception){
                Log.d("writing","fos_error")
                e.printStackTrace()
            }

            //파일 쓰기용 버퍼
            val writer:BufferedWriter = BufferedWriter(OutputStreamWriter(fos))
            //파일 쓰기
            writer.append(contents)
            writer.flush()
            writer.close()
            fos?.close()
        }catch (e:Exception){
            Log.e("writing","error occured")
            e.printStackTrace()
        }

    }
}
//   public void WriteTextFile(String foldername, String filename, String contents){
//        try{
//            File dir = new File (foldername);
//            //디렉토리 폴더가 없으면 생성함
//            if(!dir.exists()){
//                dir.mkdir();
//            }
//            //파일 output stream 생성
//            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
//            //파일쓰기
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
//            writer.write(contents);
//            writer.flush();
//
//            writer.close();
//            fos.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//}
//
//

