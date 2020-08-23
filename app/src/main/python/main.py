from java import dynamic_proxy
from java.lang import Runnable
import os
import youtube_dl as yt
from android.os import Environment
from com.arthenica.mobileffmpeg import FFmpeg

def run(activity, url, audioOnly, progressW, percentageW, velocityW, quality):
    class R(dynamic_proxy(Runnable)):
        def __init__(self):
            super(R, self).__init__()
            self.progress = 0
            self.velocity = 0
            

        def setProgress(self, progress):
            self.progress = progress

        def setVelocity(self, velocity):
            self.velocity = velocity
        
        def run(self):
                progressW.setProgress(int(round(float(self.progress))))
                percentageW.setText(str(self.progress) + "%")
                velocityW.setText(str(self.velocity))

    def updateProgress(progress):
        classToRun.setProgress(progress)

    def updateVelocity(velocity):
        classToRun.setVelocity(velocity)
    
    def update():
        activity.runOnUiThread(classToRun)

    class downloader:
        def __init__(self):
            self.file = []

        def download(self, url, filetype, path):
            # urlSplitted = url.split(":")
            # count = len(urlSplitted) - 1
            # url = urlSplitted[count-1] + ":" + urlSplitted[count]
            # url = url.replace(" ", "")
            path = str(Environment.getExternalStorageDirectory()) +"/Download/ytdl/%(title)s.%(ext)s"
            if not audioOnly:
                ydl_opts = {
                            "outtmpl": path,
                            "format": filetype,
                            "cachedir": False,
                            "ignoreerrors": True,
                            "progress_hooks": [self.my_hook]
                        }
            else:
                ydl_opts = {
                            "outtmpl": path,
                            "format": '140',
                            "cachedir": False,
                            "ignoreerrors": True,
                            "progress_hooks": [self.my_hook]
                        }
            with yt.YoutubeDL(ydl_opts) as ydl:
                ydl.download([url])
                info_dict = ydl.extract_info(url, download=False)
                video_title = str(info_dict.get('title', None))
                
                if len(self.file) == 2:
                    lastName = "\"" + str(Environment.getExternalStorageDirectory()) + "/Download/ytdl/" + video_title + ".mp4\""
                    FFmpeg.execute("-i \""+ self.file[0] +"\" -i  \""+ self.file[1] +"\" -c:v copy -c:a aac " + lastName)
                    os.remove(self.file[0])
                    os.remove(self.file[1])
                return "Done Downloading \"" + video_title + "\""

            
        def my_hook(self, d):
            if d["status"] == "finished":
                self.file.append(d["filename"])
            if d["status"] == "downloading":
                p = d["_percent_str"]
                p = p.replace("%", "")
                updateProgress(p)
                vel = d["speed"]
                if isinstance(vel, float):
                    vel = vel / (1048576)
                    updateVelocity("{:.2f}".format(vel) + " Mb/s")
                update()

    switcher = {
        '1080': 'bestvideo+bestaudio/best',
        '720': '136+bestaudio/best',
        '480': '135+bestaudio/best',
        '360': '134+bestaudio/best',
        '240': '133+bestaudio/best',
        '144': '160+bestaudio/best'
    }

    filetype = switcher.get(quality, "22")
    print(quality)
    classToRun = R()    
    down = downloader()
    val = down.download(url, filetype) 
    return val