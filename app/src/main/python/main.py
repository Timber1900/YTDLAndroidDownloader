class downloader:
    
    def __init__(self):
        self.file = []

    def test(self, url):
        import os
        import youtube_dl as yt
        from android.os import Environment
        from com.arthenica.mobileffmpeg import FFmpeg
        from android.widget import TextView
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

def run(url):
    down = downloader()
    val = down.test(url)
    return val




