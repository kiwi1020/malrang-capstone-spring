# Build
custom_build(
    # Name of the container image
    ref = 'kiwi1020/malrang-app',
    # Command to build the container image
    # On Windows, replace $EXPECTED_REF with %EXPECTED_REF%
    command = 'gradlew.bat bootBuildImage --imageName %EXPECTED_REF%',
    # Files to watch that trigger a new build
    deps = ['build.gradle', 'src']
)

# Deploy
k8s_yaml(kustomize('k8s'))

# Manage
k8s_resource('malrang-service', port_forwards=['9001'])