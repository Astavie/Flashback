{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.05";
  };

  outputs = { self, nixpkgs }:
    let
      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
      forEachSupportedSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        pkgs = import nixpkgs { inherit system; };
      });
    in
    {
      devShells = forEachSupportedSystem ({ pkgs }: {
        default = with pkgs; mkShell rec {

          buildInputs = [
            libGL
            libpulseaudio
            flite
          ];

          shellHook = ''
            export LD_LIBRARY_PATH="${lib.makeLibraryPath buildInputs}"
          '';
        };

      });
    };
}
