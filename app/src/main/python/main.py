from java import dynamic_proxy
from java.lang import Runnable
import os
import youtube_dl as yt
from android.os import Environment
from com.arthenica.mobileffmpeg import FFmpeg





def run(activity, url, textView):
    class R(dynamic_proxy(Runnable)):
        def setString(self, text):
            self.text = text
        
        def run(self):
                textView.setText(self.text)

    def updateText(text):
        classToRun.setString(text)
        activity.runOnUiThread(classToRun)

    class downloader:
        def __init__(self):
            self.file = []

        def download(self, url):
            
            path = str(Environment.getExternalStorageDirectory()) +"/Download/ytdl/%(title)s.%(ext)s"
            
            ydl_opts = {
                        "outtmpl": path,
                        "format": '137+bestaudio/best',
                        "ignoreerrors": True,
                        "cachedir": False,
                        "progress_hooks": [self.my_hook]
                    }
            with yt.YoutubeDL(ydl_opts) as ydl:
                info_dict = ydl.extract_info(url, download=True)
                video_title = str(info_dict.get('title', None))
                #ydl.download([url])
                if len(self.file) == 2:
                    lastName = "\"" + str(Environment.getExternalStorageDirectory()) + "/Download/ytdl/" + video_title + ".mp4\""
                    FFmpeg.execute("-i \""+ self.file[0] +"\" -i  \""+ self.file[1] +"\" -c:v copy -c:a aac " + lastName)
                    os.remove(self.file[0])
                    os.remove(self.file[1])
                return "Done Downloading"

            
        def my_hook(self, d):
            if d["status"] == "finished":
                self.file.append(d["filename"])
            if d["status"] == "downloading":
                updateText(d["_percent_str"])


    classToRun = R()    
    down = downloader()
    val = down.download(url) 
    return val
