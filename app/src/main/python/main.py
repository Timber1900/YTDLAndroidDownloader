def test(url):
    import youtube_dl as yt
    import os
    path = os.environ["EXTERNAL_STORAGE"]+"/Download/%(title)s.%(ext)s"

    ydl_opts = {
        "outtmpl": path,
        'format': 'bestaudio/best',
        "ignoreerrors": True,
        "cachedir": False,
    }
    with yt.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])

    return path



