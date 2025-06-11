import uuid
import os
import whisper
from yt_dlp import YoutubeDL
from transformers import pipeline

# Load models once at startup
model = whisper.load_model("base")
summarizer = pipeline("summarization", model="facebook/bart-large-cnn")

def download_audio(youtube_url):
    filename = str(uuid.uuid4())  # Unique filename without extension
    ydl_opts = {
        "format": "bestaudio/best",
        "outtmpl": filename,
        "postprocessors": [{
            "key": "FFmpegExtractAudio",
            "preferredcodec": "mp3",
            "preferredquality": "192"
        }],
        "quiet": True,
    }
    with YoutubeDL(ydl_opts) as ydl:
        ydl.download([youtube_url])
    return filename + ".mp3"

def transcribe(audio_path):
    print(f"Transcription de l'audio: {audio_path}")
    result = model.transcribe(audio_path)
    text = result.get("text", "")
    print(f"Texte transcrit (longueur {len(text)}): {text[:100]}...")
    return text

def summarize(text, max_length=200, min_length=50):
    if not text.strip():
        print("Le texte est vide, résumé impossible.")
        return "No text to summarize."

    input_len = len(text)
    if input_len < min_length:
        max_length = input_len
        min_length = max(1, input_len // 2)
        print(f"Texte trop court pour résumé standard, ajustement max_length={max_length}, min_length={min_length}")

    try:
        result = summarizer(text, max_length=max_length, min_length=min_length, do_sample=False)
        summary = result[0]['summary_text']
        print(f"Résumé généré: {summary}")
        return summary
    except Exception as e:
        print(f"Erreur lors du résumé: {e}")
        return f"Erreur lors du résumé: {e}"

# Example usage for local testing
if __name__ == "__main__":
    url = "https://www.youtube.com/watch?v=GT3Doklq0RY"  # Test URL
    audio_file = download_audio(url)
    text = transcribe(audio_file)
    summary = summarize(text)
    print("Résumé final :", summary)

    # Clean up audio file
    if os.path.exists(audio_file):
        os.remove(audio_file)
