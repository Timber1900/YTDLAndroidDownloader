class downloader:
    
    def __init__(self):
        self.file = []

    def test(self, url):
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
            ydl.download([url])
            return self.file

        
    def my_hook(self, d):
        if d["status"] == "finished":
            self.file.append(d["filename"])
        if d["status"] == "downloading":
            pass

def run(url):
    down = downloader()
    val = down.test(url)
    return val




