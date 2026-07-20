export type ApiResponse<T> = {
    success: boolean; 
    message: string;
    data: T | null;
    errorCode: string | null;
    timestamp: string;
};

export type ArtifactClassificationResponse = {
    classificationId: string;
    artifactType: string;
    confidence: number;
    reasoningSummary: string;
    modelUsed: string;
    latencyMs: number;
    createdAt: string;
};

export type ArtifactUploadResponse = {
    artifactId: string;
    originalFileName: string;
    fileType: string;
    fileSizeBytes: number;
    uploadStatus: string;
    createdAt: string;
    classification: ArtifactClassificationResponse | null;
}