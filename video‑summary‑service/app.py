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
        # Download and process the video audio
        audio_file = download_audio(youtube_url)
        transcript = transcribe(audio_file)
        summary_text = summarize(transcript)
    except Exception as e:
        return jsonify({ "error": str(e) }), 500
    finally:
        # Clean up audio file if exists
        if 'audio_file' in locals() and os.path.exists(audio_file):
            os.remove(audio_file)

    # Return the summary only
    return jsonify({
        "summary": summary_text
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
