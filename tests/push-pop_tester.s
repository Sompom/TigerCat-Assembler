

movd %arg1 $0x111111
pushd %arg1

movd %arg1 $0x222222
pushd %arg1

movd %arg1 $0x333333
pushd %arg1

pushd $0x444444
pushd $0x555555

popd %ret1
popd %ret2
popd %arg1
popd %arg2
popd %arg3
