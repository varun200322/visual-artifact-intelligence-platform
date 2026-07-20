import { useState } from "react";
import { uploadArtifact, ArtifactUploadError } from "../api/artifactApi";
import type { ArtifactUploadResponse } from "../types/artifact";
import { ErrorAlert } from "./ErrorAlert";
import { ClassificationResultCard } from "./ClassificationResultCard";

const ALLOWED_TYPES = ["image/png", "image/jpeg", "image/webp"];
const MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;

export function ArtifactUploadForm() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [result, setResult] = useState<ArtifactUploadResponse | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [errorCode, setErrorCode] = useState<string | null>(null);
  const [isUploading, setIsUploading] = useState(false);

  function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0] ?? null;

    setResult(null);
    setErrorMessage(null);
    setErrorCode(null);

    if (!file) {
      setSelectedFile(null);
      return;
    }

    const validationError = validateFile(file);

    if (validationError) {
      setSelectedFile(null);
      setErrorMessage(validationError);
      setErrorCode("CLIENT_FILE_VALIDATION");
      return;
    }

    setSelectedFile(file);
  }

  async function handleUpload(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selectedFile) {
      setErrorMessage("Please select a valid image file before uploading.");
      setErrorCode("CLIENT_NO_FILE");
      return;
    }

    setIsUploading(true);
    setErrorMessage(null);
    setErrorCode(null);
    setResult(null);

    try {
      const response = await uploadArtifact(selectedFile);

      if (!response.data) {
        setErrorMessage("Backend returned an empty response.");
        setErrorCode("CLIENT_EMPTY_RESPONSE");
        return;
      }

      setResult(response.data);
    } catch (error) {
      if (error instanceof ArtifactUploadError) {
        setErrorMessage(error.message);
        setErrorCode(error.errorCode);
      } else {
        setErrorMessage("Unexpected frontend error occurred during upload.");
        setErrorCode("CLIENT_UNKNOWN_ERROR");
      }
    } finally {
      setIsUploading(false);
    }
  }

  return (
    <main className="page">
      <section className="hero">
        <p className="eyebrow">Multimodal AI Research Project</p>
        <h1>Visual Artifact Intelligence Platform</h1>
        <p>
          Upload a graph, chart, table, or diagram. The backend stores the
          artifact, calls the FastAPI AI service, and returns a structured
          classification result.
        </p>
      </section>

      <section className="upload-card">
        <form onSubmit={handleUpload}>
          <label htmlFor="artifact-file">Upload visual artifact</label>

          <input
            id="artifact-file"
            type="file"
            accept="image/png,image/jpeg,image/webp"
            onChange={handleFileChange}
            disabled={isUploading}
          />

          {selectedFile && (
            <div className="file-preview">
              <span className="label">Selected file</span>
              <p>{selectedFile.name}</p>
              <p>{formatBytes(selectedFile.size)}</p>
            </div>
          )}

          <button type="submit" disabled={!selectedFile || isUploading}>
            {isUploading ? "Uploading and classifying..." : "Upload Artifact"}
          </button>
        </form>
      </section>

      {errorMessage && (
        <ErrorAlert message={errorMessage} errorCode={errorCode} />
      )}

      {result && <ClassificationResultCard result={result} />}
    </main>
  );
}

function validateFile(file: File): string | null {
  if (!ALLOWED_TYPES.includes(file.type)) {
    return "Only PNG, JPEG, and WebP image files are supported.";
  }

  if (file.size > MAX_FILE_SIZE_BYTES) {
    return "File size must be 5MB or less.";
  }

  return null;
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} B`;
  }

  const kb = bytes / 1024;

  if (kb < 1024) {
    return `${kb.toFixed(2)} KB`;
  }

  const mb = kb / 1024;
  return `${mb.toFixed(2)} MB`;
}