from flask import Flask, request, jsonify
from transcribe_and_summarize import download_audio, transcribe, summarize
import os
from flask_cors import CORS

app = Flask(__name__)
CORS(app, origins=["http://localhost:4200"])

@app.route('/api/summary', methods=['POST'])
def summary():
    data = request.json
    youtube_url = data.get('youtubeUrl')
    if not youtube_url:
        return jsonify({ "error": "youtubeUrl is required" }), 400

    try:
        audio_file = download_audio(youtube_url)
        transcript = transcribe(audio_file)
        summary_text = summarize(transcript)
        os.remove(audio_file)  # Nettoyage
    except Exception as e:
        return jsonify({ "error": str(e) }), 500

    return jsonify({
        'transcript': transcript,
        'summary': summary_text
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
