# JPF Installation

## Installation

### Requirements

- JDK 8
- Ant

### Install JPF Core
1. clone jpf-core from https://github.com/grzesuav/jpf-core 
2. Go to the directory and type: `ant`

### Install JPF Symb
1. clone jpf-symbc from https://github.com/grzesuav/jpf-symbc
2. go to the cloned directory and type `ant`

### Install Z3
1. clone z3 for java (```git clone https://github.com/Z3Prover/z3```)
2. build z3 (```python scripts/mk_make.py --java;cd build;make```)

### Create properties

1. define a `DYLD_LIBRARY_PATH` (for mac) `LD_LIBRARY_PATH` (for linux) with this value: `${project_path}/lib:$DYLD_LIBRARY_PATH:<path_to_z3_build_folder>`
2. create ```~/.jpf/site.properties```
3. put in ```~/.jpf/site.properties```

```property
# JPF site configuration

jpf-core = <path_to_jpf>/jpf-core

# numeric extension
jpf-symbc = <path_to_jpf>/jpf-symbc

extensions=${jpf-core},${jpf-symbc}
```

## Execution