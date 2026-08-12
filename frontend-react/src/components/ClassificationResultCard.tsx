import type { ArtifactUploadResponse } from "../types/artifact";

type ClassificationResultCardProps = {
  result: ArtifactUploadResponse;
};

export function ClassificationResultCard({
  result,
}: ClassificationResultCardProps) {
  const classification = result.classification;

  return (
    <section className="result-card">
      <h2>Artifact Processed</h2>

      <div className="grid">
        <div>
          <span className="label">Artifact ID</span>
          <p>{result.artifactId}</p>
        </div>

        <div>
          <span className="label">Original File</span>
          <p>{result.originalFileName}</p>
        </div>

        <div>
          <span className="label">File Type</span>
          <p>{result.fileType}</p>
        </div>

        <div>
          <span className="label">File Size</span>
          <p>{formatBytes(result.fileSizeBytes)}</p>
        </div>

        <div>
          <span className="label">Upload Status</span>
          <p>{result.uploadStatus}</p>
        </div>

        <div>
          <span className="label">Created At</span>
          <p>{new Date(result.createdAt).toLocaleString()}</p>
        </div>
      </div>

      {classification ? (
        <div className="classification-box">
          <h3>Classification Result</h3>

          <div className="grid">
            <div>
              <span className="label">Artifact Type</span>
              <p>{classification.artifactType}</p>
            </div>

            <div>
              <span className="label">Confidence</span>
              <p>{Math.round(classification.confidence * 100)}%</p>
            </div>

            <div>
              <span className="label">Model Used</span>
              <p>{classification.modelUsed}</p>
            </div>

            <div>
              <span className="label">AI Latency</span>
              <p>{classification.latencyMs} ms</p>
            </div>
          </div>

          <div>
            <span className="label">Reasoning Summary</span>
            <p>{classification.reasoningSummary}</p>
          </div>
        </div>
      ) : (
        <p>No classification result available.</p>
      )}


      <div className="questions-box">
        <h3>Recommended Questions</h3>

        {result.recommendedQuestions.length > 0 ? (
        <ol className="question-list">
        {result.recommendedQuestions.map((question) => (
        <li key={question.questionId} className="question-item">
          <p>{question.questionText}</p>

          <div className="question-meta">
            <span>Difficulty: {question.difficulty}</span>
            <span>Skill: {question.skillTag}</span>
            <span>Source: {question.source}</span>
          </div>
        </li>
      ))}
    </ol>
  ) : (
    <p>No curated SQL questions found for this artifact type.</p>
  )}
</div>
    </section>
  );
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