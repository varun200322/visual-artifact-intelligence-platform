import type { ApiResponse, ArtifactUploadResponse } from "../types/artifact";   

const BACKEND_BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL ?? "http://localhost:8080";

export async function uploadArtifact(
    file: File
): Promise<ApiResponse<ArtifactUploadResponse>> {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`${BACKEND_BASE_URL}/api/v1/artifacts/upload`, {
        method: "POST",
        body: formData,
    });

    const body = (await response.json()) as ApiResponse<ArtifactUploadResponse>;

    if(!response.ok) {
        throw new ArtifactUploadError(
            body.message || "Artifact upload failed",
            body.errorCode,
            response.status
        );
    }

    return body;
}

export class ArtifactUploadError extends Error {
    errorCode: string | null;
    status: number;

    constructor(message: string, errorCode: string | null, status:number) {
        super(message);
        this.name = "ArtifactUploadError";
        this.errorCode = errorCode;
        this.status = status;
    }
}