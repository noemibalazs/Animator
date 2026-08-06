#pragma once

#include "onnxruntime_c_api.h"
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

/** Hides OrtEnv/OrtStatus/OrtSession from Kotlin cinterop (inaccessible from iosMain via commonizer). */
static inline void* animator_ort_get_api(void) {
    return (void*)OrtGetApiBase()->GetApi(ORT_API_VERSION);
}

static inline void* animator_ort_create_env(void* api_ptr, void** out_env) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    OrtEnv* env = NULL;
    OrtStatus* status = api->CreateEnv(ORT_LOGGING_LEVEL_WARNING, "animator", &env);
    if (out_env) {
        *out_env = env;
    }
    return status;
}

static inline const char* animator_ort_get_error_message(void* api_ptr, void* status) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    return api->GetErrorMessage((const OrtStatus*)status);
}

static inline void animator_ort_release_status(void* api_ptr, void* status) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    api->ReleaseStatus((OrtStatus*)status);
}

static inline void animator_ort_release_env(void* api_ptr, void* env) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    api->ReleaseEnv((OrtEnv*)env);
}

static inline void* animator_ort_create_session(
    void* api_ptr,
    void* env,
    const char* model_path,
    void** out_session
) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    OrtSessionOptions* options = NULL;
    OrtStatus* status = api->CreateSessionOptions(&options);
    if (status) {
        return status;
    }

    OrtSession* session = NULL;
    status = api->CreateSession((const OrtEnv*)env, model_path, options, &session);
    api->ReleaseSessionOptions(options);
    if (out_session) {
        *out_session = session;
    }
    return status;
}

static inline void animator_ort_release_session(void* api_ptr, void* session) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    api->ReleaseSession((OrtSession*)session);
}

static inline void* animator_ort_session_input_name(
    void* api_ptr,
    void* session,
    size_t index,
    char* buf,
    size_t buf_len
) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    OrtAllocator* allocator = NULL;
    OrtStatus* status = api->GetAllocatorWithDefaultOptions(&allocator);
    if (status) {
        return status;
    }

    char* name = NULL;
    status = api->SessionGetInputName((const OrtSession*)session, index, allocator, &name);
    if (status) {
        return status;
    }

    if (buf && buf_len > 0) {
        if (name) {
            strncpy(buf, name, buf_len - 1);
            buf[buf_len - 1] = '\0';
        } else {
            buf[0] = '\0';
        }
    }
    api->AllocatorFree(allocator, name);
    return NULL;
}

static inline void* animator_ort_session_output_name(
    void* api_ptr,
    void* session,
    size_t index,
    char* buf,
    size_t buf_len
) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    OrtAllocator* allocator = NULL;
    OrtStatus* status = api->GetAllocatorWithDefaultOptions(&allocator);
    if (status) {
        return status;
    }

    char* name = NULL;
    status = api->SessionGetOutputName((const OrtSession*)session, index, allocator, &name);
    if (status) {
        return status;
    }

    if (buf && buf_len > 0) {
        if (name) {
            strncpy(buf, name, buf_len - 1);
            buf[buf_len - 1] = '\0';
        } else {
            buf[0] = '\0';
        }
    }
    api->AllocatorFree(allocator, name);
    return NULL;
}

/**
 * Creates an OrtValue tensor backed by caller-owned float data.
 * `data` must remain valid until the returned value is released / Run completes.
 */
static inline void* animator_ort_create_float_tensor(
    void* api_ptr,
    float* data,
    size_t element_count,
    int64_t* shape,
    size_t rank,
    void** out
) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    OrtMemoryInfo* memory_info = NULL;
    OrtStatus* status = api->CreateCpuMemoryInfo(OrtArenaAllocator, OrtMemTypeDefault, &memory_info);
    if (status) {
        return status;
    }

    OrtValue* value = NULL;
    status = api->CreateTensorWithDataAsOrtValue(
        memory_info,
        data,
        element_count * sizeof(float),
        shape,
        rank,
        ONNX_TENSOR_ELEMENT_DATA_TYPE_FLOAT,
        &value
    );
    api->ReleaseMemoryInfo(memory_info);

    if (out) {
        *out = value;
    }
    return status;
}

static inline void* animator_ort_run(
    void* api_ptr,
    void* session,
    const char* input_name,
    void* input_tensor,
    const char* output_name,
    void** out_tensor
) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    const char* input_names[] = {input_name};
    const char* output_names[] = {output_name};
    const OrtValue* inputs[] = {(const OrtValue*)input_tensor};
    OrtValue* outputs[] = {NULL};

    OrtStatus* status = api->Run(
        (OrtSession*)session,
        NULL,
        input_names,
        inputs,
        1,
        output_names,
        1,
        outputs
    );

    if (out_tensor) {
        *out_tensor = outputs[0];
    }
    return status;
}

static inline void* animator_ort_tensor_floats(
    void* api_ptr,
    void* tensor,
    float* out,
    size_t count
) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    void* data = NULL;
    OrtStatus* status = api->GetTensorMutableData((OrtValue*)tensor, &data);
    if (status) {
        return status;
    }
    if (out && data && count > 0) {
        memcpy(out, data, count * sizeof(float));
    }
    return NULL;
}

static inline void animator_ort_release_value(void* api_ptr, void* value) {
    const OrtApi* api = (const OrtApi*)api_ptr;
    api->ReleaseValue((OrtValue*)value);
}

#ifdef __cplusplus
}
#endif
