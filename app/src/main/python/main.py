from java import dynamic_proxy
from java.lang import Runnable


class downloader:
    def __init__(self, activity):
        self.file = []
        self.activity = activity

    def download(self, url, activity):
        import os
        import youtube_dl as yt
        from android.os import Environment
        from com.arthenica.mobileffmpeg import FFmpeg

        class R(dynamic_proxy(Runnable)):
            def run(self):
                textView.setText("Test on python")
                
        self.activity.runOnUiThread(R())

        path = str(Environment.getExternalStorageDirectory()) +"/Download/ytdl/%(title)s.%(ext)s"
        
        ydl_opts = {
                    "outtmpl": path,
                    "format": '137+bestaudio/best',
                    "ignoreerrors": True,
                    "cachedir": False,
                    "progress_hooks": [self.my_hook]
                }
        with yt.YoutubeDL(ydl_opts) as ydl:
            info_dict = ydl.extract_info(url, download=False)
            video_title = str(info_dict.get('title', None))
            ydl.download([url])
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
            pass

def run(activity, url, textView):
    
    down = downloader(activity)
    val = down.download(url) 
    return val
