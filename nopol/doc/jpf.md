# JPF Installation

## Installation

1. clone jpf-core and jpf-symbc into a jpf folder
2. clone z3 for java (```git clone https://github.com/Z3Prover/z3```)
3. build z3 (```python scripts/mk_make.py --java;cd build;make```)
4. define a `DYLD_LIBRARY_PATH` (for mac) `LD_LIBRARY_PATH` (for linux) with this value: `${project_path}/lib:$DYLD_LIBRARY_PATH:<path_to_z3_build_folder>`
5. create ```~/.jpf/site.properties```
6. put in ```~/.jpf/site.properties```

```property
# JPF site configuration

jpf-core = <path_to_jpf>/jpf-core

# numeric extension
jpf-symbc = <path_to_jpf>/jpf-symbc

extensions=${jpf-core},${jpf-symbc}
```

## Execution