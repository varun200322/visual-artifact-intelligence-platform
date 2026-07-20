type ErrorAlertProps = {
    message: string;
    errorCode?: string | null;
  };
  
  export function ErrorAlert({ message, errorCode }: ErrorAlertProps) {
    return (
      <div className="error-alert">
        <strong>Upload failed</strong>
        <p>{message}</p>
        {errorCode && <span>Error code: {errorCode}</span>}
      </div>
    );
  }